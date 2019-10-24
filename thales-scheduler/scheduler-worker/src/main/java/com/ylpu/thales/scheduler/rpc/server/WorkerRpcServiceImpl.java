package com.ylpu.thales.scheduler.rpc.server;

import com.ylpu.thales.scheduler.WorkerServer;
import com.ylpu.thales.scheduler.core.config.Configuration;
import com.ylpu.thales.scheduler.core.rpc.entity.JobInstanceRequestRpc;
import com.ylpu.thales.scheduler.core.rpc.entity.JobInstanceResponseRpc;
import com.ylpu.thales.scheduler.core.rpc.entity.JobRequestRpc;
import com.ylpu.thales.scheduler.core.rpc.service.GrpcJobServiceGrpc;
import com.ylpu.thales.scheduler.core.utils.DateUtils;
import com.ylpu.thales.scheduler.core.utils.MetricsUtils;
import com.ylpu.thales.scheduler.enums.AlertType;
import com.ylpu.thales.scheduler.enums.JobType;
import com.ylpu.thales.scheduler.enums.TaskState;
import com.ylpu.thales.scheduler.executor.AbstractCommonExecutor;
import com.ylpu.thales.scheduler.executor.ExecutorManager;
import com.ylpu.thales.scheduler.executor.listener.Event;
import com.ylpu.thales.scheduler.executor.listener.ITaskListener;
import com.ylpu.thales.scheduler.request.JobInstanceRequest;

import io.grpc.stub.StreamObserver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WorkerRpcServiceImpl extends GrpcJobServiceGrpc.GrpcJobServiceImplBase {
    
    private static Log LOG = LogFactory.getLog(WorkerRpcServiceImpl.class);
    private IJobMetric jobMetric;
    private List<ITaskListener> listeners = new ArrayList<ITaskListener>();
    
    public void addListener(ITaskListener listener) {
        listeners.add(listener);
    }
    
    public IJobMetric getJobMetric() {
        return jobMetric;
    }
    
    public void setJobMetric(IJobMetric jobMetric) {
        this.jobMetric = jobMetric;
    }
    
    public void submit(JobInstanceRequestRpc requestRpc,StreamObserver<JobInstanceResponseRpc> responseObserver) {
        
        JobInstanceResponseRpc.Builder builder = JobInstanceResponseRpc.newBuilder();
        builder.setResponseId(requestRpc.getRequestId());
        JobInstanceRequest request = new JobInstanceRequest();
        initJobInstanceRequest(requestRpc,request);
        try {
            //增加任务个数
            jobMetric.increaseTask();
            //执行任务
            LOG.info("start to execute task " + requestRpc.getId());
            AbstractCommonExecutor executor = getExecutor(requestRpc,request);
            executor.execute();
            //设置任务成功返回状态
            setCodeAndMessage(builder,TaskState.SUCCESS.getCode(),200,"");
        }catch(Exception e) {
            LOG.error(e);
          //设置任务失败返回状态
            setCodeAndMessage(builder,TaskState.FAIL.getCode(),500,
                    "failed to execute task " + requestRpc.getId());
            //任务失败告警
            Event event = new Event();
            event.setException(e.getMessage());
            setAlertEvent(event,requestRpc.getJob(),request);
            alertFailure(event);
        }finally {
            //减少任务个数
            jobMetric.decreaseTask();
        }
        //rpc任务返回
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
    
    private void alertFailure(Event event) {
        for(ITaskListener listener : listeners) {
            listener.onFailure(event);
        }
    }
    
    private void setAlertEvent(Event event,JobRequestRpc requestRpc,JobInstanceRequest request) {
        event.setTaskId(requestRpc.getId());
        event.setAlertType(AlertType.getAlertType(requestRpc.getAlertTypes()));
        event.setAlertUsers(requestRpc.getAlertUsers());
        event.setLogUrl(request.getLogUrl());
        event.setHostName(request.getWorker());
    }
    
    public void kill(JobInstanceRequestRpc requestRpc,StreamObserver<JobInstanceResponseRpc> responseObserver) {
        JobInstanceResponseRpc.Builder builder = JobInstanceResponseRpc.newBuilder();
        builder.setResponseId(requestRpc.getRequestId());
        JobInstanceRequest request = new JobInstanceRequest();
        initJobInstanceRequest(requestRpc,request);
        LOG.info("start to kill task " + requestRpc.getId());
        try {
            AbstractCommonExecutor executor = getExecutor(requestRpc,request);
            executor.kill();
            jobMetric.decreaseTask();
            setCodeAndMessage(builder,TaskState.KILL.getCode(),200,"");
        }catch(Exception e) {
            LOG.error(e);
            setCodeAndMessage(builder,TaskState.RUNNING.getCode(),500,
                    "failed to kill task" + requestRpc.getId());
        }
        //rpc任务返回
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
    
    private void setCodeAndMessage(JobInstanceResponseRpc.Builder builder,int state,int code,String message) {
        builder.setTaskState(state);
        builder.setErrorMsg(message);
        builder.setErrorCode(code);
    }
    
    private AbstractCommonExecutor getExecutor(JobInstanceRequestRpc requestRpc,JobInstanceRequest request) throws Exception{
        Class<?> cls = ExecutorManager.getExecutor(JobType.getJobType(requestRpc.getJob().getJobType()));
        AbstractCommonExecutor executor = (AbstractCommonExecutor) cls.getConstructor(JobInstanceRequestRpc.class,JobInstanceRequest.class)
                .newInstance(requestRpc,request);
        return executor;
    }
    
    private void initJobInstanceRequest(JobInstanceRequestRpc requestRpc,JobInstanceRequest request) {
        request.setApplicationid(requestRpc.getApplicationid());
        request.setCreatorEmail(requestRpc.getCreatorEmail());
        request.setCreatorName(requestRpc.getCreatorName());
        request.setScheduleTime(DateUtils.getDatetime(requestRpc.getScheduleTime()));
        request.setStartTime(DateUtils.getDatetime(requestRpc.getStartTime()));
        request.setElapseTime(requestRpc.getElapseTime());
        request.setLogPath(requestRpc.getLogPath());
        request.setLogUrl(requestRpc.getLogUrl());
        request.setPid(requestRpc.getPid());
        request.setRetryTimes(requestRpc.getRetryTimes());
        request.setTaskState(requestRpc.getTaskState());
        Properties prop = Configuration.getConfig();
        int wokerServerPort = Configuration.getInt(prop,"thales.worker.server.port",
        		WorkerServer.DEFAULT_WORKER_SERVER_PORT);
        request.setWorker(MetricsUtils.getHostIpAddress() + ":" + wokerServerPort);
        request.setJobId(requestRpc.getJob().getId());
        request.setId(requestRpc.getId());
    }
}