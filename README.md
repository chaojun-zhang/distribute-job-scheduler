# Introduction
thales是一款自主研发的调度系统，系统支持调度shell,hive,spark等各种任务类型。

# Architecture
![image](https://github.com/ylpu/distribute-job-scheduler/tree/master/docs/thales-arch.png)

# 目录结构

## 1. Project structure 

- scheduler
	- alert --任务告警
	- api --对外接口
		- controller --任务控制层
		- dao --任务数据层
		- service --任务服务层
		- common --通用帮助类
		--model --对外接口模型
	- master --任务调度
	    - quartz --任务扫描调度
	    - master manager --任务资源管理
	    - task schedule & submit --任务提交调度
	- worker --任务执行
	    - log --任务日志
	    - execute thread -- 任务执行
	- core --核心实现
	- common --通用帮助类
# scheduler-master
master作为调度的核心，主要有如下功能：
1.初始化任务实例状态
2.通过quartz调度所有任务
3.检查任务状态
4.启动master rpc服务供worker汇报资源和心跳
5.master ha
6.启动jetty server接受api的调用

# scheduler-worker
worker作为执行器，主要有如下功能：
1.负责任务的执行，目前可执行shell,hive,spark任务
2.启动jetty server供查看任务日志，
3.上报资源信息，发送心跳给master.

# scheduler-api
api作为接口层，主要有如下功能：
1.和前端交互，负责任务调度，下线，重跑，看日志等。
