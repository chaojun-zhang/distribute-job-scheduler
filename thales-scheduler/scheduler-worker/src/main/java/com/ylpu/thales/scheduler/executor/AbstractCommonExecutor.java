package com.ylpu.thales.scheduler.executor;

import com.google.protobuf.ByteString;
import com.ylpu.thales.scheduler.WorkerServer;
import com.ylpu.thales.scheduler.core.config.Configuration;
import com.ylpu.thales.scheduler.core.rest.JobManager;
import com.ylpu.thales.scheduler.core.rpc.entity.JobInstanceRequestRpc;
import com.ylpu.thales.scheduler.core.rpc.entity.JobRequestRpc;
import com.ylpu.thales.scheduler.core.rpc.entity.JobStatusRequestRpc;
import com.ylpu.thales.scheduler.core.utils.ByteUtils;
import com.ylpu.thales.scheduler.core.utils.DateUtils;
import com.ylpu.thales.scheduler.core.utils.FileUtils;
import com.ylpu.thales.scheduler.core.utils.MetricsUtils;
import com.ylpu.thales.scheduler.core.utils.TaskProcessUtils;
import com.ylpu.thales.scheduler.core.zk.ZKHelper;
import com.ylpu.thales.scheduler.enums.TaskState;
import com.ylpu.thales.scheduler.request.JobInstanceRequest;
import com.ylpu.thales.scheduler.rpc.client.WorkerGrpcClient;
import com.ylpu.thales.scheduler.rpc.server.WorkerRpcServiceImpl;

import java.io.File;
import java.util.Properties;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractCommonExecutor{
	
	private static Log LOG = LogFactory.getLog(AbstractCommonExecutor.class);
    
    private static final int LOG_SERVER_PORT = 9099;
    
    private static final String DEFAULT_LOG_DIR = "/tmp/log/worker";
        
    private JobInstanceRequest request;
    
    private JobInstanceRequestRpc requestRpc;
    
    public AbstractCommonExecutor (JobInstanceRequestRpc requestRpc,JobInstanceRequest request){
        this.requestRpc = requestRpc;
        this.request = request;
    }
        
    public void execute() throws Exception{
        
        Properties prop = Configuration.getConfig();
        int logServerPort = Configuration.getInt(prop, "thales.log.server.port", LOG_SERVER_PORT);
        
        String logDir = Configuration.getString(prop, "thales.worker.log.path", DEFAULT_LOG_DIR);
        String logPath = logDir + File.separator + requestRpc.getJob().getId() + "-" + request.getId() + "-" + 
                DateUtils.getDateAsString(request.getStartTime(),DateUtils.TIME_FORMAT);
        String logOutPath = logPath + ".out";
        String logErrorPath = logPath + ".error";

        String command = buildCommand(requestRpc.getJob().getJobConfiguration());
        Process process = Runtime.getRuntime().exec(command);
        FileUtils.writeOuput(process.getInputStream(),logOutPath);
        FileUtils.writeOuput(process.getErrorStream(),logErrorPath);
        Long pid = TaskProcessUtils.getLinuxPid(process);
        
        request.setLogPath(logOutPath);
        request.setLogUrl("http://" + MetricsUtils.getHostIpAddress() + ":" + logServerPort
                + "/api/log/viewLog/" + requestRpc.getId());
        request.setPid(pid.intValue());
        request.setTaskState(TaskState.RUNNING.getCode());
        
        //修改任务状态
        JobStatusRequestRpc jobStatusRequestRpc = buildJobStatus(requestRpc.getRequestId(),
        		TaskState.RUNNING,request);
        int returnCode = updateJobStatus(jobStatusRequestRpc);
        
        if(returnCode != 200) {
            process.destroy();
            throw new RuntimeException("failed to update task for " + requestRpc.getId());
        }        
        int c = process.waitFor();
        if(c != 0){
            throw new RuntimeException("failed to execute task " + requestRpc.getId());
        }
    }
    
    private JobStatusRequestRpc buildJobStatus(String requestId,TaskState taskState,JobInstanceRequest request) {
        JobStatusRequestRpc.Builder builder = JobStatusRequestRpc.newBuilder();
        builder.setRequestId(requestId);
        builder.setTaskState(taskState.getCode());
        builder.setData(ByteString.copyFrom(ByteUtils.objectToByteArray(request)));
        return builder.build();
    }
    
    private int updateJobStatus(JobStatusRequestRpc request) {
        WorkerGrpcClient client = null;
        int returnCode = 200;
        try {
            String master = ZKHelper.getActiveMaster();
            if(StringUtils.isNoneBlank(master)) {
               String[] hostAndPort =  master.split(":");
               client = new WorkerGrpcClient(hostAndPort[0],NumberUtils.toInt(hostAndPort[1]));
               returnCode = client.updateJobStatus(request);
            }  
         }catch(Exception e) {
        	 returnCode = 500;
             LOG.error(e); 
         }finally {
             if(client != null) {
                 try {
                     client.shutdown();
                 } catch (InterruptedException e) {
                	 returnCode = 500;
                     LOG.error(e);
                 }
             }
         }
        return returnCode;
    }
    
    public abstract void kill() throws Exception;
    
    public abstract String buildCommand(String configFile) throws Exception;
}