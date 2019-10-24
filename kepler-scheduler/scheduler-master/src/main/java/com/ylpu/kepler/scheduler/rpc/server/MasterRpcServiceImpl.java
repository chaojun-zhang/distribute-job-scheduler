package com.ylpu.kepler.scheduler.rpc.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ylpu.kepler.scheduler.core.rest.JobManager;
import com.ylpu.kepler.scheduler.core.rest.WorkerManager;
import com.ylpu.kepler.scheduler.core.rpc.entity.JobInstanceResponseRpc;
import com.ylpu.kepler.scheduler.core.rpc.entity.JobStatusRequestRpc;
import com.ylpu.kepler.scheduler.core.rpc.entity.WorkerParameter;
import com.ylpu.kepler.scheduler.core.rpc.entity.WorkerRequestRpc;
import com.ylpu.kepler.scheduler.core.rpc.entity.WorkerResponseRpc;
import com.ylpu.kepler.scheduler.core.rpc.service.GrpcWorkerServiceGrpc;
import com.ylpu.kepler.scheduler.core.utils.ByteUtils;
import com.ylpu.kepler.scheduler.core.utils.DateUtils;
import com.ylpu.kepler.scheduler.manager.MasterManager;
import com.ylpu.kepler.scheduler.request.JobInstanceRequest;
import com.ylpu.kepler.scheduler.request.WorkerRequest;
import com.ylpu.kepler.scheduler.rpc.client.JobCallBackScan;

import io.grpc.stub.StreamObserver;

public class MasterRpcServiceImpl extends GrpcWorkerServiceGrpc.GrpcWorkerServiceImplBase {
    
    private static Log LOG = LogFactory.getLog(MasterRpcServiceImpl.class);
    /**
     * 更新节点内存，cpu等信息
     */
    public void updateResource(WorkerRequestRpc request,StreamObserver<WorkerResponseRpc> responseObserver) {
        LOG.info("update resource for host " + request.getHost()  + 
                " at " + DateUtils.getDatetime(request.getLastHeartbeatTime()));
        WorkerResponseRpc.Builder builder = WorkerResponseRpc.newBuilder(); 
        WorkerRequest workerRequest =  new WorkerRequest();
        setWorkerInfo(request,workerRequest);
        try {
            MasterManager.getInstance().updateResource(workerRequest);
            WorkerManager.insertOrUpdateWorker(workerRequest);
            builder.setErrorCode(200);
            builder.setErrorMsg("");
        }catch(Exception e) {
            builder.setErrorCode(500);
            builder.setErrorMsg(e.getMessage());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
    
    /**
     * 增加worker任务个数
     */
    public void incTask(WorkerParameter parameter,StreamObserver<WorkerResponseRpc> responseObserver) {
        WorkerResponseRpc.Builder builder = WorkerResponseRpc.newBuilder(); 
        try {
            MasterManager.getInstance().increaseTask(parameter.getHostname());
            builder.setErrorCode(200);
            builder.setErrorMsg("");
        }catch(Exception e) {
            builder.setErrorCode(500);
            builder.setErrorMsg(e.getMessage());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
    
    /**
     * 减少worker任务个数
     */
    public void decTask(WorkerParameter parameter,StreamObserver<WorkerResponseRpc> responseObserver) {
        WorkerResponseRpc.Builder builder = WorkerResponseRpc.newBuilder(); 
        try {
            MasterManager.getInstance().decreaseTask(parameter.getHostname());
            builder.setErrorCode(200);
            builder.setErrorMsg("");
        }catch(Exception e) {
            builder.setErrorCode(500);
            builder.setErrorMsg(e.getMessage());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
    
    /**
     * 更新任务状态
     */
    public void updateJobStatus(JobStatusRequestRpc request,StreamObserver<WorkerResponseRpc> responseObserver) {
        WorkerResponseRpc.Builder builder = WorkerResponseRpc.newBuilder(); 
        try {
        	Object obj = ByteUtils.byteArrayToObject(request.getData().toByteArray());
        	if(obj instanceof JobInstanceRequest) {
        		JobInstanceRequest jobInstanceRequest = (JobInstanceRequest)obj;
        		JobManager.updateJobInstanceSelective(jobInstanceRequest);
        	}
            JobInstanceResponseRpc responseRpc = setJobStatus(request);
            JobCallBackScan.addResponse(responseRpc);
            builder.setErrorCode(200);
            builder.setErrorMsg("");
        }catch(Exception e) {
            builder.setErrorCode(500);
            builder.setErrorMsg(e.getMessage());
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
    
    private JobInstanceResponseRpc setJobStatus(JobStatusRequestRpc requestRpc) {
        return JobInstanceResponseRpc.newBuilder()
        .setResponseId(requestRpc.getRequestId())
        .setErrorCode(200)
        .setTaskState(requestRpc.getTaskState())
        .setErrorMsg("")
        .build();
    }
    
    private void setWorkerInfo(WorkerRequestRpc requestRpc,WorkerRequest workerRequest) {
        workerRequest.setId(requestRpc.getId());
        workerRequest.setHost(requestRpc.getHost());
        workerRequest.setPort(requestRpc.getPort());
        workerRequest.setLastHeartbeatTime(DateUtils.getDatetime(requestRpc.getLastHeartbeatTime()));
        workerRequest.setCpuUsage(requestRpc.getCpuUsage());
        workerRequest.setMemoryUsage(requestRpc.getMemoryUsage());
        workerRequest.setZkdirectory(requestRpc.getZkdirectory());
        workerRequest.setNodeGroup(requestRpc.getNodeGroup());
        workerRequest.setNodeType(requestRpc.getNodeType());
        workerRequest.setNodeStatus(requestRpc.getNodeStatus());
    }
}