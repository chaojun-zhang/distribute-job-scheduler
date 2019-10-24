package com.ylpu.thales.scheduler.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ylpu.thales.scheduler.common.dao.BaseDao;
import com.ylpu.thales.scheduler.common.rest.ScheduleManager;
import com.ylpu.thales.scheduler.common.service.impl.BaseServiceImpl;
import com.ylpu.thales.scheduler.dao.SchedulerJobMapper;
import com.ylpu.thales.scheduler.dao.SchedulerJobRelationMapper;
import com.ylpu.thales.scheduler.entity.SchedulerJob;
import com.ylpu.thales.scheduler.entity.SchedulerJobRelation;
import com.ylpu.thales.scheduler.enums.JobReleaseState;
import com.ylpu.thales.scheduler.request.JobRequest;
import com.ylpu.thales.scheduler.request.ScheduleRequest;
import com.ylpu.thales.scheduler.response.JobResponse;
import com.ylpu.thales.scheduler.response.JobTree;
import com.ylpu.thales.scheduler.service.JobService;
import com.ylpu.thales.scheduler.service.exception.ThalesRuntimeException;

@Service
@Transactional
public class JobServiceImpl extends BaseServiceImpl<SchedulerJob,Integer> implements JobService {

    @Autowired
    private SchedulerJobMapper schedulerJobMapper;
    
    @Autowired
    private SchedulerJobRelationMapper schedulerJobRelationMapper;

    @Override
    protected BaseDao<SchedulerJob, Integer> getDao() {
        return schedulerJobMapper;
    }

	@Override
    public void addJob(JobRequest job) {		
        SchedulerJob schedulerJob = new SchedulerJob();
        if(job != null) {
            BeanUtils.copyProperties(job, schedulerJob);
            insertSelective(schedulerJob);
        }
        SchedulerJobRelation sr = null; 
        if(job.getDependencies() != null && job.getDependencies().size() > 0) {
           for(Integer parentJobId : job.getDependencies()) {
                sr = new SchedulerJobRelation();
                sr.setJobId(schedulerJob.getId());
                sr.setParentjobId(parentJobId);
                schedulerJobRelationMapper.insertSelective(sr);
           }
       }
    }
	
    @Override
    public void updateJob(JobRequest job) {        
        SchedulerJob schedulerJob = new SchedulerJob();
        if(job != null) {
            BeanUtils.copyProperties(job, schedulerJob);
            updateByPrimaryKeySelective(schedulerJob);
        }
        
        if(job.getDependencies() != null && job.getDependencies().size() > 0) {
            schedulerJobRelationMapper.deleteByJobId(job.getId());
            SchedulerJobRelation sr = null;
            for(Integer parentJobId : job.getDependencies()) {
                sr = new SchedulerJobRelation();
                sr.setJobId(job.getId());
                sr.setParentjobId(parentJobId);
                schedulerJobRelationMapper.insertSelective(sr);
            }
        }
    }	
    
    public JobTree queryTreeById(Integer id) {
        JobTree targetTree = new JobTree();
        com.ylpu.thales.scheduler.entity.JobTree sourceTree =  schedulerJobMapper.queryTreeById(id);
        if(sourceTree != null) {
            setResponse(sourceTree,targetTree); 
        }
        return targetTree;
    }
    
    private void setResponse(com.ylpu.thales.scheduler.entity.JobTree sourceTree,JobTree targetTree) {
        List<JobTree> targetList = new ArrayList<JobTree>();
        setTarget(sourceTree,targetTree);
        targetTree.setChildren(targetList);
        
        List<com.ylpu.thales.scheduler.entity.JobTree> sourceList = sourceTree.getChildren();
        for(com.ylpu.thales.scheduler.entity.JobTree source : sourceList) {
            JobTree target = new JobTree();
            setTarget(source,target);
            targetList.add(target);
            setResponse(source,target);
        }
    }
    
    private void setTarget(com.ylpu.thales.scheduler.entity.JobTree source,JobTree target) {
        target.setJobCycle(source.getJobCycle());
        target.setScheduleCron(source.getScheduleCron());
        target.setJobId(source.getJobId());
        target.setParentJobId(source.getParentJobId());
    }

	@Override
	public JobResponse getJobAndRelationById(Integer id) {
	    JobResponse response = new JobResponse();
	    List<SchedulerJob> jobList = schedulerJobMapper.getJobParentsByIds(Arrays.asList(id));
	    if(jobList == null || jobList.size() == 0) {
	       return null;
	    }
		SchedulerJob schedulerJob = jobList.get(0);
		if(schedulerJob != null) {
		      BeanUtils.copyProperties(schedulerJob, response);
		        List<SchedulerJobRelation> dependencies = schedulerJob.getRelations();
		        List<Integer> ids = dependencies.stream().map(t -> t.getParentjobId()).collect(Collectors.toList());
		        List<JobResponse> list = new ArrayList<JobResponse>();
		        if(ids != null && ids.size() > 0) {
		            List<SchedulerJob> jobs = getJobAndDependencyByIds(ids);
		            if(jobs != null && jobs.size() > 0) {
		                JobResponse dependency = null;
		                for(SchedulerJob job  : jobs) {
		                    dependency = new JobResponse();
		                    BeanUtils.copyProperties(job, dependency);
		                    list.add(dependency);
		                }
		            }  
		        }
		        response.setDependencies(list); 
		}
		return response;
	}
	
    private List<SchedulerJob> getJobAndDependencyByIds(List<Integer> ids){
        return schedulerJobMapper.getJobParentsByIds(ids);
    }

    @Override
    public void scheduleJob(ScheduleRequest request) {
        int status = ScheduleManager.scheduleJob(getMasterServiceUri(request.getId()), request);
        if(status != HttpStatus.NO_CONTENT.value()) {
            throw new ThalesRuntimeException("error occurs,can not schedule job " + request.getId());
        }
    }
    
    @Override
    public void rescheduleJob(ScheduleRequest request) {
        int status = ScheduleManager.rescheduleJob(getMasterServiceUri(request.getId()), request);
        if(status != HttpStatus.NO_CONTENT.value()) {
            throw new ThalesRuntimeException("error occurs,can not reschedule job " + request.getId());
        }
    }
    
    @Override
    public void downJob(ScheduleRequest request) {
        
        SchedulerJob schedulerJob = new SchedulerJob();
        schedulerJob.setId(request.getId());
        schedulerJob.setJobReleasestate(JobReleaseState.OFFLINE.getCode());
        updateByPrimaryKeySelective(schedulerJob);
        
        int status = ScheduleManager.downJob(getMasterServiceUri(request.getId()), request);
        if(status != HttpStatus.NO_CONTENT.value()) {
            throw new ThalesRuntimeException("error occurs,can not down job " + request.getId());
        }
    }
}