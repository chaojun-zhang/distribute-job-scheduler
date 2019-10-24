package com.ylpu.kepler.scheduler.core.rest;

import com.ylpu.kepler.scheduler.core.config.Configuration;
import com.ylpu.kepler.scheduler.core.constants.GlobalConstants;
import com.ylpu.kepler.scheduler.request.WorkerGroupRequest;
import com.ylpu.kepler.scheduler.request.WorkerRequest;
import com.ylpu.kepler.scheduler.response.SchedulerResponse;

import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;

public class WorkerManager {
    
    public static List<Map<String,Object>> getTaskCountByWorker() {
        ParameterizedTypeReference<SchedulerResponse<List<Map<String,Object>>>> typeRef = new ParameterizedTypeReference<SchedulerResponse<List<Map<String,Object>>>>() {};
        String apiUrl = Configuration.getString(Configuration.getConfig(),
                "kepler.api.url",GlobalConstants.DEFAULT_API_URL);
        SchedulerResponse<List<Map<String,Object>>> schedulerResponse = RestClient.getForObject(apiUrl + "jobInstance/getRunningJobCount",typeRef,null);
        return schedulerResponse.getData();
    }
    
    public static void updateWorkersStatusByGroup(WorkerGroupRequest param) {
        String apiUrl = Configuration.getString(Configuration.getConfig(),
                "kepler.api.url",GlobalConstants.DEFAULT_API_URL);
        RestClient.post(apiUrl +"worker/updateWorkersStatusByGroup",param,SchedulerResponse.class);
    }
    
    public static void insertOrUpdateWorker(WorkerRequest workerRequest) {
        String apiUrl = Configuration.getString(Configuration.getConfig(),
                "kepler.api.url",GlobalConstants.DEFAULT_API_URL);
        RestClient.post(apiUrl +"worker/insertOrUpdateWorker",workerRequest,SchedulerResponse.class);
    }
}