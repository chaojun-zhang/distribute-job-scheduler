package com.ylpu.kepler.scheduler.rest.service;

import com.ylpu.kepler.scheduler.core.constants.GlobalConstants;
import com.ylpu.kepler.scheduler.core.rest.JobManager;
import com.ylpu.kepler.scheduler.core.rpc.entity.JobInstanceRequestRpc;
import com.ylpu.kepler.scheduler.core.utils.CronUtils;
import com.ylpu.kepler.scheduler.core.utils.DateUtils;
import com.ylpu.kepler.scheduler.enums.GrpcType;
import com.ylpu.kepler.scheduler.enums.TaskState;
import com.ylpu.kepler.scheduler.manager.JobScheduler;
import com.ylpu.kepler.scheduler.manager.JobSubmission;
import com.ylpu.kepler.scheduler.manager.SchedulerJob;
import com.ylpu.kepler.scheduler.manager.TaskCall;
import com.ylpu.kepler.scheduler.request.JobInstanceRequest;
import com.ylpu.kepler.scheduler.request.ScheduleRequest;
import com.ylpu.kepler.scheduler.response.JobInstanceResponse;
import com.ylpu.kepler.scheduler.response.JobResponse;
import com.ylpu.kepler.scheduler.response.JobTree;
import com.ylpu.kepler.scheduler.rest.entity.JobScheduleInfo;
import com.ylpu.kepler.scheduler.rpc.client.AbstractJobGrpcClient;
import com.ylpu.kepler.scheduler.rpc.client.JobCallBackScan;
import com.ylpu.kepler.scheduler.rpc.client.JobGrpcBlockingClient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Calendar;
import java.util.Date;

public class SchedulerService {
    
    private static Log LOG = LogFactory.getLog(SchedulerService.class);
    
    /**
     * 杀任务
     * @param response
     * @throws Exception
     */
    public void killJob(ScheduleRequest request) throws Exception{
        try {
            JobInstanceResponse response = JobManager.getJobInstanceById(request.getId());
            if(response.getTaskState() != TaskState.RUNNING.getCode()) {
                throw new RuntimeException("can not kill job " + request.getId() + " because job is not running ");
            }
            String worker = response.getWorker();
            if(StringUtils.isNotBlank(worker)) {
                JobInstanceRequestRpc rpcJobInstanceRequest = setRequest(response);
                String[] hostAndPort = worker.split(":");
                AbstractJobGrpcClient client = null;
                try {
                    client = new JobGrpcBlockingClient(hostAndPort[0],NumberUtils.toInt(hostAndPort[1]));
                    client.kill(rpcJobInstanceRequest); 
                }finally {
                    if(client != null) {
                        client.shutdown();
                    }
                }
            }
        }catch(Exception e) {
            LOG.error(e);
            throw e;
        }
    }
    
    private JobInstanceRequestRpc setRequest(JobInstanceResponse response ) {
        JobInstanceRequestRpc newRpcJobInstanceRequest = JobInstanceRequestRpc.newBuilder()
                .setApplicationid(response.getApplicationid())
                .setPid(response.getPid())
                .setId(response.getId())
                .setRequestId(response.getJobConf().getId() + "-" + 
                DateUtils.getDateAsString(response.getScheduleTime(),DateUtils.TIME_FORMAT))
                .setJob(JobSubmission.setJobRequest(response.getJobConf()))
                .setCreatorEmail(response.getCreatorEmail())
                .setCreatorName(response.getCreatorName())
                .setScheduleTime(DateUtils.getProtobufTime(response.getScheduleTime()))
                .setStartTime(DateUtils.getProtobufTime(response.getStartTime()))
                .setLogPath(response.getLogPath())
                .setLogUrl(response.getLogUrl())
                .setRetryTimes(response.getRetryTimes())
                .setTaskState(response.getTaskState())
                .setWorker(response.getWorker())
                .build();
        return newRpcJobInstanceRequest;
    }
    
    /**
     * 任务调度
     * @param response
     * @throws Exception
     */
    public void scheduleJob(ScheduleRequest request) throws Exception {
        try {
            JobScheduleInfo scheduleInfo = new JobScheduleInfo();
            JobResponse response = JobManager.getJobById(request.getId());
            setScheduleInfo(response,scheduleInfo);
            JobScheduler.addJob(scheduleInfo, SchedulerJob.class);
        }catch(Exception e) {
            LOG.error(e);
            throw e;
        }
    }
    /**
     * 任务时间修改后重新调度
     * @param response
     * @throws Exception
     */
    public void rescheduleJob(ScheduleRequest request) throws Exception {
        try {
            JobScheduleInfo scheduleInfo = new JobScheduleInfo();
            JobResponse response = JobManager.getJobById(request.getId());
            setScheduleInfo(response,scheduleInfo);
            JobScheduler.modifyJobTime(scheduleInfo);
        }catch(Exception e) {
            LOG.error(e);
            throw e;
        }
    }
    /**
     * 任务下线
     * @param response
     * @throws Exception
     */
    public void downJob(ScheduleRequest request) throws Exception {
        try {
            JobResponse response = JobManager.getJobById(request.getId());
            JobScheduleInfo scheduleInfo = new JobScheduleInfo();
            setScheduleInfo(response,scheduleInfo);
            JobScheduler.removeJob(scheduleInfo);
        }catch(Exception e) {
            LOG.error(e);
            throw e;
        }

    }
    
    /**
     * 重跑当前任务
     * @param response
     * @throws Exception
     */
    public void rerun(Integer id) throws Exception {
        try {
            JobInstanceResponse jobInstanceResponse = JobManager.getJobInstanceById(id);
            //初始化任务
            JobInstanceRequest request = new JobInstanceRequest();
            JobSubmission.initJobInstance(request,jobInstanceResponse.getJobConf());
            request.setId(jobInstanceResponse.getId());
            request.setRetryTimes(jobInstanceResponse.getRetryTimes() + 1);
            request.setScheduleTime(jobInstanceResponse.getScheduleTime());
            request.setStartTime(new Date());
            request.setCreateTime(new Date());
            JobManager.updateJobInstanceByKey(request);
            
            JobCallBackScan.addResponse(JobSubmission.buildJobStatus(
                    jobInstanceResponse.getJobConf().getId(),jobInstanceResponse.getScheduleTime(),TaskState.SUBMIT));
            
            JobInstanceRequestRpc rpcRequest = JobSubmission.initJobInstanceRequestRpc(request,
                    jobInstanceResponse.getJobConf());
            
            JobSubmission.addTask(new TaskCall(rpcRequest,GrpcType.ASYNC));
            
        }catch(Exception e) {
            LOG.error(e);
            throw e;
        }
    }
    
    /**
     * 重跑当前任务以及下游所有任务
     * @param scheduleRequest
     * @throws Exception
     */
    public void rerunAll(Integer id) throws Exception {
        try {
            rerun(id);
            JobInstanceResponse jobInstanceResponse = JobManager.getJobInstanceById(id);
            if(jobInstanceResponse != null) {
                JobTree jobTree = JobManager.queryTreeById(jobInstanceResponse.getJobConf().getId());
                if(jobTree != null && jobTree.getChildren() != null) {
                    for(JobTree child : jobTree.getChildren()) {
                        rerunChild(jobInstanceResponse.getScheduleTime(),child);
                    }
                }
            }
        }catch(Exception e) {
            LOG.error(e);
            throw e;
        }
    }
    
    /**
     * 重跑子任务
     * @param startScheduleTime
     * @param jobTree
     * @throws Exception
     */
    private void rerunChild(Date startScheduleTime,JobTree jobTree) throws Exception {
        try {
            String scheduleTime  = caculateJobScheduleTime(startScheduleTime,jobTree.getScheduleCron());
            Integer instanceId = JobManager.getInstanceIdByTime(jobTree.getJobId(),scheduleTime);
            if(instanceId != null) {
                rerun(instanceId);
            }
            if(jobTree.getChildren() != null) {
                for(JobTree child : jobTree.getChildren()) {
                    rerunChild(startScheduleTime,child);
                }
            }
        }catch(Exception e) {
            LOG.error(e);
            throw e;
        }
    }
    
    /**
     * 计算子任务的schedule time
     * @param startScheduleTime
     * @param currentScheduleCron
     * @return
     */
    private String caculateJobScheduleTime(Date startScheduleTime,String currentScheduleCron) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.YEAR, 1);
        return CronUtils.getNextTriggerTime(currentScheduleCron,startScheduleTime,calendar.getTime());
    }
    
    private void setScheduleInfo(JobResponse response,JobScheduleInfo scheduleInfo) {
        scheduleInfo.setCron(response.getScheduleCron());
        scheduleInfo.setJobName(response.getJobName());
        scheduleInfo.setJobGroupName(GlobalConstants.DEFAULT_SCHEDULER_JOB_GROUP);
        scheduleInfo.setTriggerName(response.getJobName());
        scheduleInfo.setTriggerGroupName(GlobalConstants.DEFAULT_SCHEDULER_TRIGGER_GROUP);
        scheduleInfo.setData(response);
    }
}