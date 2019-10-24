package com.ylpu.thales.scheduler.core.rest;

import com.ylpu.thales.scheduler.core.config.Configuration;
import com.ylpu.thales.scheduler.core.constants.GlobalConstants;
import com.ylpu.thales.scheduler.request.JobInstanceRequest;
import com.ylpu.thales.scheduler.request.JobStatusRequest;
import com.ylpu.thales.scheduler.response.*;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobManager {
    
    /**
     * 返回更新成功与否标识,200表示成功，500表示失败
     * @param request
     * @return
     */
    public static int updateJobInstanceSelective(JobInstanceRequest request) {
        String apiUrl = Configuration.getString(Configuration.getConfig(),
                "thales.api.url",GlobalConstants.DEFAULT_API_URL);
        @SuppressWarnings("rawtypes")
        ResponseEntity<SchedulerResponse> response = RestClient.post(apiUrl + "jobInstance/updateJobInstanceSelective",request,SchedulerResponse.class);
        @SuppressWarnings("unchecked")
        SchedulerResponse<Void> schedulerResponse = response.getBody();
        return schedulerResponse.getErrorCode();
    }
    
    /**
     * 返回更新成功与否标识,200表示成功，500表示失败
     * @param request
     * @return
     */
    public static int updateJobInstanceByKey(JobInstanceRequest request) {
        String apiUrl = Configuration.getString(Configuration.getConfig(),
                "thales.api.url",GlobalConstants.DEFAULT_API_URL);
        @SuppressWarnings("rawtypes")
        ResponseEntity<SchedulerResponse> response = RestClient.post(apiUrl + "jobInstance/updateJobInstanceByKey",request,SchedulerResponse.class);
        @SuppressWarnings("unchecked")
        SchedulerResponse<Void> schedulerResponse = response.getBody();
        return schedulerResponse.getErrorCode();
    }
    
    /**
     * 返回更新成功与否标识,200表示成功，500表示失败
     * @param request
     * @return
     */
    public static int updateJobStatus(JobStatusRequest request) {
        String apiUrl = Configuration.getString(Configuration.getConfig(),
                "thales.api.url",GlobalConstants.DEFAULT_API_URL);
        @SuppressWarnings("rawtypes")
        ResponseEntity<SchedulerResponse> response = RestClient.post(apiUrl + "jobInstance/updateJobStatus",request,SchedulerResponse.class);
        @SuppressWarnings("unchecked")
        SchedulerResponse<Void> schedulerResponse = response.getBody();
        return schedulerResponse.getErrorCode();
    }
    
    /**
     * 返回更新成功与否标识,200表示成功，500表示失败
     * @param request
     * @return
     */
    public static int markAsFailed(List<JobInstanceRequest> list) {
        String apiUrl = Configuration.getString(Configuration.getConfig(),
                "thales.api.url",GlobalConstants.DEFAULT_API_URL);
        @SuppressWarnings("rawtypes")
        ResponseEntity<SchedulerResponse> response = RestClient.post(apiUrl + "jobInstance/markAsFailed",list,SchedulerResponse.class);
        @SuppressWarnings("unchecked")
        SchedulerResponse<Void> schedulerResponse = response.getBody();
        return schedulerResponse.getErrorCode();
    }
    
    /**
     * 返回添加任务实例的id
     * @param request
     * @return
     */
    public static Integer addJobInstance(JobInstanceRequest request) {
        String apiUrl = Configuration.getString(Configuration.getConfig(),
                "thales.api.url",GlobalConstants.DEFAULT_API_URL);
        @SuppressWarnings("rawtypes")
        ResponseEntity<SchedulerResponse> response =  RestClient.post(apiUrl +"jobInstance/addJobInstance",request,SchedulerResponse.class);
        SchedulerResponse<?> schedulerResponse = response.getBody();
        String instanceId = String.valueOf(schedulerResponse.getData());
        return NumberUtils.toInt(instanceId);
    }
    
    /**
     * 获取所有任务实例的状态，用于master恢复。
     * @return
     */
    public static List<JobInstanceStateResponse> getAllJobStatus() {
        String apiUrl = Configuration.getString(Configuration.getConfig(),
                "thales.api.url",GlobalConstants.DEFAULT_API_URL);
        ParameterizedTypeReference<SchedulerResponse<List<JobInstanceStateResponse>>> typeRef = new ParameterizedTypeReference<SchedulerResponse<List<JobInstanceStateResponse>>>() {};
        SchedulerResponse<List<JobInstanceStateResponse>> response = RestClient.getForObject(apiUrl +"jobInstance/getAllJobStatus",typeRef,null);
        return response.getData();
    }
    /**
     * 根据id查看任务实例
     * @param id
     * @return
     */
    public static JobInstanceResponse getJobInstanceById(Integer id) {
        String apiUrl = Configuration.getString(Configuration.getConfig(),
                "thales.api.url",GlobalConstants.DEFAULT_API_URL);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("id", id);
        ParameterizedTypeReference<SchedulerResponse<JobInstanceResponse>> typeRef = new ParameterizedTypeReference<SchedulerResponse<JobInstanceResponse>>() {};
        SchedulerResponse<JobInstanceResponse> jobInstanceResponse = RestClient.getForObject(apiUrl +"jobInstance/getJobInstanceById", typeRef,map);
        return jobInstanceResponse.getData();
    }
    /**
     * 根据id查看任务
     * @param id
     * @return
     */
    public static JobResponse getJobById(Integer id) {
        String apiUrl = Configuration.getString(Configuration.getConfig(),
                "thales.api.url",GlobalConstants.DEFAULT_API_URL);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("id", id);
        ParameterizedTypeReference<SchedulerResponse<JobResponse>> typeRef = new ParameterizedTypeReference<SchedulerResponse<JobResponse>>() {};
        SchedulerResponse<JobResponse> jobResponse = RestClient.getForObject(apiUrl +"job/getJobById", typeRef,map);
        return jobResponse.getData();
    }
    /**
     * 根据任务id查看任务树状结构
     * @param id
     * @return
     */
    public static JobTree queryTreeById(Integer id) {
        String apiUrl = Configuration.getString(Configuration.getConfig(),
                "thales.api.url",GlobalConstants.DEFAULT_API_URL);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("id", id);
        ParameterizedTypeReference<SchedulerResponse<JobTree>> typeRef = new ParameterizedTypeReference<SchedulerResponse<JobTree>>() {};
        SchedulerResponse<JobTree> jobResponse = RestClient.getForObject(apiUrl +"job/queryTreeById", typeRef,map);
        return jobResponse.getData();
    }
    /**
     * 根据schedule时间和任务id查看任务实例id
     * @param jobId
     * @param scheduleTime
     * @return
     */
    public static Integer getInstanceIdByTime(Integer jobId,String scheduleTime) {
        String apiUrl = Configuration.getString(Configuration.getConfig(),
                "thales.api.url",GlobalConstants.DEFAULT_API_URL);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("jobId", jobId);
        map.put("scheduleTime", scheduleTime);
        ParameterizedTypeReference<SchedulerResponse<Integer>> typeRef = new ParameterizedTypeReference<SchedulerResponse<Integer>>() {};
        SchedulerResponse<Integer> jobResponse = RestClient.getForObject(apiUrl +"jobInstance/getInstanceIdByTime", typeRef,map);
        return jobResponse.getData();
    }
}