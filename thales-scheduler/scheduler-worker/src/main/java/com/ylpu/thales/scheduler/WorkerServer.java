package com.ylpu.thales.scheduler;

import com.ylpu.thales.scheduler.core.config.Configuration;
import com.ylpu.thales.scheduler.core.constants.GlobalConstants;
import com.ylpu.thales.scheduler.core.rpc.entity.WorkerRequestRpc;
import com.ylpu.thales.scheduler.core.utils.DateUtils;
import com.ylpu.thales.scheduler.core.utils.MetricsUtils;
import com.ylpu.thales.scheduler.core.zk.ZKHelper;
import com.ylpu.thales.scheduler.enums.NodeType;
import com.ylpu.thales.scheduler.enums.WorkerStatus;
import com.ylpu.thales.scheduler.log.LogServer;
import com.ylpu.thales.scheduler.rpc.client.WorkerGrpcClient;
import com.ylpu.thales.scheduler.rpc.server.WorkerRpcServer;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Properties;

public class WorkerServer {
    
    private static Log LOG = LogFactory.getLog(WorkerServer.class);
    
    public static int DEFAULT_WORKER_SERVER_PORT = 8090;
    
    private WorkerRpcServer rpcServer;
    
    private LogServer logServer;
    
    private ZkClient zkClient;
    
    public static final String DEFAULT_WORKER_GROUP = "default";
    
    public static final long WORKER_HEARTBEAT_INTERVAL = 3000;

    private static volatile boolean stop = false;
    
    public void start() {
        Properties prop = Configuration.getConfig();
        int workerServerPort = Configuration.getInt(prop,"thales.worker.server.port",DEFAULT_WORKER_SERVER_PORT);
        String workerGroup = Configuration.getString(prop, "thales.worker.group", DEFAULT_WORKER_GROUP);
        long heartBeatInterval = Configuration.getLong(prop,"thales.worker.heartbeat.interval",WORKER_HEARTBEAT_INTERVAL);
        try {
            Runtime.getRuntime().addShutdownHook(new ShutDownHookThread());
            //注册自己到zk
            String workerPath = regist(prop);
            //启动心跳线程
            WorkerHeartBeatThread heartBeatThread = new WorkerHeartBeatThread(
                    workerPath,workerGroup,workerServerPort,heartBeatInterval);
            heartBeatThread.setDaemon(true);
            heartBeatThread.start();
            //启动日志服务
            logServer = new LogServer(prop);
            logServer.startLogServer();
            //启动rpc服务
            rpcServer = new WorkerRpcServer(workerServerPort);
            rpcServer.startServer();
            rpcServer.blockUntilShutdown();
        }catch(Exception e) {
            LOG.error(e);
            System.exit(1);
        }
    }
    
    private class ShutDownHookThread extends Thread{
        @Override
        public void run() {
            if(logServer != null) {
                LOG.warn("*** shutting down log server since JVM is shutting down");
                logServer.stop();
                LOG.warn("*** log server shut down");
            }
            if(rpcServer != null) {
                LOG.warn("*** shutting down gRPC server since JVM is shutting down");
                rpcServer.shutdownNow();
                LOG.warn("*** rpc server shut down");
            }
            if(zkClient != null) {
                LOG.warn("*** close zkClient since JVM is shutting down");
                zkClient.close();
                LOG.warn("*** close zkclient");
            }
            LOG.warn("*** stop heartbeat since JVM is shutting down");
            stopHeartBeat();
        }
    }
    
    public static void main(String[] args){
        WorkerServer jobExecutorServer = new WorkerServer();
        jobExecutorServer.start();
    } 
    
    private String regist(Properties prop) {
        String quorum = prop.getProperty("thales.zookeeper.quorum");
        int sessionTimeout = Configuration.getInt(prop, "thales.zookeeper.sessionTimeout", GlobalConstants.ZOOKEEPER_SESSION_TIMEOUT);
        int connectionTimeout = Configuration.getInt(prop, "thales.zookeeper.connectionTimeout", GlobalConstants.ZOOKEEPER_CONNECTION_TIMEOUT);
        String group = GlobalConstants.WORKER_GROUP;
        String workerGroup = Configuration.getString(prop, "thales.worker.group", DEFAULT_WORKER_GROUP);
        
        zkClient = ZKHelper.getClient(quorum,sessionTimeout,connectionTimeout);
        List<String> root = zkClient.getChildren("/");
        if(root == null || !root.contains(GlobalConstants.THALES)) {
            ZKHelper.createNode(zkClient, GlobalConstants.ROOT_GROUP, null);
        }
        List<String> workers = zkClient.getChildren(GlobalConstants.ROOT_GROUP);
        if(workers == null || !workers.contains(GlobalConstants.WORKERS)) {
           ZKHelper.createNode(zkClient, group, null);          
        }
        List<String> groups = zkClient.getChildren(group);
        if(groups == null || !groups.contains(workerGroup)) {
            ZKHelper.createNode(zkClient, group + "/" + workerGroup, null);
        }
        String workerPath = group + "/" + workerGroup + "/" + MetricsUtils.getHostIpAddress();
        ZKHelper.createEphemeralNode(zkClient, workerPath, null);
        return workerPath;
    }
    
    public void stopHeartBeat() {
        if(stop == false) {
            stop = true;
        }
    }
    
    private static class WorkerHeartBeatThread extends Thread{
        private String workerPath;
        private String workerGroup;
        private int workerPort;
        private long heartBeatInterval;
        
        public WorkerHeartBeatThread(String workerPath,String workerGroup,int workerPort,long heartBeatInterval) {
            this.workerPath = workerPath;
            this.workerGroup = workerGroup; 
            this.workerPort = workerPort;
            this.heartBeatInterval = heartBeatInterval;
        }
        public void run() {
            WorkerGrpcClient client = null;
            while(!stop) {
                try {
                    String master = ZKHelper.getActiveMaster();
                    String[] masters = master.split(":");
                    client = new WorkerGrpcClient(masters[0],NumberUtils.toInt(masters[1]));
                    WorkerRequestRpc request = WorkerRequestRpc.newBuilder()
                            .setHost(MetricsUtils.getHostIpAddress())
                            .setCpuUsage(MetricsUtils.getCpuUsage())
                            .setMemoryUsage(MetricsUtils.getMemoryUsage())
                            .setNodeGroup(workerGroup)
                            .setNodeStatus(WorkerStatus.UPDATED.getCode())
                            .setNodeType(NodeType.WORKER.getCode())
                            .setPort(workerPort)
                            .setZkdirectory(workerPath)
                            .setLastHeartbeatTime(DateUtils.getProtobufTime())
                        .build();
                    client.updateResource(request);
                } catch (Exception e) {
                    LOG.error(e);
                }finally {
                    try {
                        if(client != null) {
                            client.shutdown();
                        }
                    } catch (Exception e) {
                        LOG.error(e);
                    }
                }
                try {
                    Thread.sleep(heartBeatInterval);
                } catch (InterruptedException e) {
                    LOG.error(e);
                }
            }
        }
    }
}