package com.dwj.seckill.manager;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
/**
 * @ClassName QuartzManager
 * @Description
 * @Author v-dangwj
 * @Date 2019/3/4 17:03
 */
@Component
public class BaseJobManager {

    /**
     * 注入任务调度器
     */
    @Autowired
    private Scheduler scheduler;
    /**
     * 默认任务组
     */
    private static final String JOB_DEFAULT_GROUP_NAME = "JOB_DEFAULT_GROUP";
    /**
     * 默认触发器组
     */
    private static final String TRIGGER_DEFAULT_GROUP_NAME = "TRIGGER_DEFAULT_GROUP";

    /**
     * 添加一个Cron定时任务(使用默认的任务组名，触发器名，触发器组名)(立即启动)
     * @param jobName
     * @param jobClass
     * @param cronExpression
     * @throws SchedulerException
     */
    public void createCronJob(String jobName, Class<? extends Job> jobClass, String cronExpression) throws SchedulerException {
        createCronJob(jobName,JOB_DEFAULT_GROUP_NAME,jobName,TRIGGER_DEFAULT_GROUP_NAME,jobClass,cronExpression);
    }

    /**
     * 添加一个带参数Cron定时任务(使用默认的任务组名，触发器名，触发器组名)(立即启动)
     * @param jobName
     * @param jobClass
     * @param cronExpression
     * @param parameter
     * @throws SchedulerException
     */
    public void createCronJob(String jobName, Class<? extends Job> jobClass, String cronExpression, Map<String, Object> parameter) throws SchedulerException {
        createCronJob(jobName,JOB_DEFAULT_GROUP_NAME,jobName,TRIGGER_DEFAULT_GROUP_NAME,jobClass,cronExpression,parameter);
    }

    /**
     * 添加一个Simple任务(使用默认的任务组名，触发器名，触发器组名)
     * @param jobName 任务名称
     * @param jobClass 任务Class
     * @param startDate 开始时间
     * @param interval 执行间隔
     * @param timeUnit 执行单位 只支持(秒/分/时)
     * @param count 重复次数
     */
    public void createSimpleJob(String jobName, Class<? extends Job> jobClass,Date startDate,Integer interval, TimeUnit timeUnit, Integer count) throws SchedulerException {
        createSimpleJob(jobName,JOB_DEFAULT_GROUP_NAME,jobName,TRIGGER_DEFAULT_GROUP_NAME,jobClass,startDate,interval,timeUnit,count);
    }

    /**
     * 添加一个带参数Simple任务(使用默认的任务组名，触发器名，触发器组名)
     * @param jobName 任务名称
     * @param jobClass 任务Class
     * @param startDate 开始时间
     * @param interval 执行间隔
     * @param timeUnit 执行单位 只支持(秒/分/时)
     * @param count 重复次数
     * @param parameter 参数
     */
    public void createSimpleJob(String jobName, Class<? extends Job> jobClass,Date startDate,Integer interval, TimeUnit timeUnit, Integer count, Map<String, Object> parameter) throws SchedulerException {
        createSimpleJob(jobName,JOB_DEFAULT_GROUP_NAME,jobName,TRIGGER_DEFAULT_GROUP_NAME,jobClass,startDate,interval,timeUnit,count,parameter);
    }

    /**
     * 添加一个执行一次Simple任务(使用默认的任务组名，触发器名，触发器组名)
     * @param jobName
     * @param jobClass
     * @param startDate
     * @throws SchedulerException
     */
    public void createOnceSimpleJob(String jobName, Class<? extends Job> jobClass, Date startDate) throws SchedulerException {
        createOnceSimpleJob(jobName,JOB_DEFAULT_GROUP_NAME,jobName,TRIGGER_DEFAULT_GROUP_NAME,jobClass,startDate);
    }

    /**
     * 添加一个执行一次带参数Simple任务(使用默认的任务组名，触发器名，触发器组名)
     * @param jobName
     * @param jobClass
     * @param startDate
     * @param parameter
     * @throws SchedulerException
     */
    public void createOnceSimpleJob(String jobName, Class<? extends Job> jobClass, Date startDate, Map<String, Object> parameter) throws SchedulerException {
        createOnceSimpleJob(jobName,JOB_DEFAULT_GROUP_NAME,jobName,TRIGGER_DEFAULT_GROUP_NAME,jobClass,startDate,parameter);
    }

    /**
     * 添加一个Cron定时任务(立即启动)
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @param jobClass
     * @param cronExpression
     * @throws SchedulerException
     */
    public void createCronJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
                              Class<? extends Job> jobClass, String cronExpression) throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroupName).build();
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, triggerGroupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 添加一个带参数Cron定时任务(立即启动)
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @param jobClass
     * @param cronExpression
     * @param parameter
     * @throws SchedulerException
     */
    public void createCronJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
                              Class<? extends Job> jobClass, String cronExpression, Map<String, Object> parameter) throws SchedulerException {
            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(jobName, jobGroupName).build();
            for (Map.Entry<String, Object> entry : parameter.entrySet()) {
                jobDetail.getJobDataMap().put(entry.getKey(),entry.getValue());
            }
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerName, triggerGroupName)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
            scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 修改一个任务的cron表达式(使用默认的任务组名，触发器名，触发器组名)
     * @param jobName
     * @param cronExpression
     * @throws SchedulerException
     */
    public void modifyDefaultJobCronExpression(String jobName, String cronExpression) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, TRIGGER_DEFAULT_GROUP_NAME);
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if (trigger == null) {
            return;
        }
        String oldCronExpression = trigger.getCronExpression();
        if (!oldCronExpression.equalsIgnoreCase(cronExpression)) {
            JobKey jobKey = JobKey.jobKey(jobName, JOB_DEFAULT_GROUP_NAME);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            Class<? extends Job> objJobClass = jobDetail.getJobClass();
            removeDefaultJob(jobName);
            createCronJob(jobName, objJobClass, cronExpression);
        }
    }

    /**
     * 修改一个Cron任务的触发时间
     * @param triggerName
     * @param triggerGroupName
     * @param cronExpression
     * @throws SchedulerException
     */
    public void modifyJobCronExpression(String triggerName, String triggerGroupName, String cronExpression) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        if (trigger == null) {
            return;
        }
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(trigger.getCronExpression());
        String oldCronExpression = trigger.getCronExpression();
        if (!oldCronExpression.equalsIgnoreCase(cronExpression)) {
            trigger = trigger.getTriggerBuilder()
                    .withIdentity(triggerKey).withSchedule(scheduleBuilder)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
            scheduler.rescheduleJob(triggerKey, trigger);
        }
    }

    /**
     * 添加一个Simple任务
     * @param jobName 任务名称
     * @param jobGroupName 任务组
     * @param triggerName 触发器名称
     * @param triggerGroupName 触发器组
     * @param jobClass 任务class
     * @param startDate 第一次开始时间
     * @param interval 执行间隔
     * @param timeUnit 执行单位 只支持(秒/分/时)
     * @param count 重复次数
     * @throws SchedulerException
     */
    public void createSimpleJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Class<? extends Job> jobClass, Date startDate,Integer interval, TimeUnit timeUnit, Integer count) throws SchedulerException {
//        if(startDate.getTime() <= System.currentTimeMillis()){
//            throw new IllegalArgumentException("第一次开始时间必须大于当前时间");
//        }
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroupName).build();
        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, triggerGroupName).startAt(startDate)
                .withSchedule(executionRule(interval,timeUnit,count)).build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 添加一个带参数Simple任务
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @param jobClass
     * @param startDate
     * @param interval
     * @param timeUnit
     * @param count
     * @param parameter
     * @throws SchedulerException
     */
    private void createSimpleJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Class<? extends Job> jobClass, Date startDate, Integer interval, TimeUnit timeUnit, Integer count, Map<String, Object> parameter) throws SchedulerException {
//        if(startDate.getTime() <= System.currentTimeMillis()){
//            throw new IllegalArgumentException("第一次开始时间必须大于当前时间");
//        }
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroupName).build();
        for (Map.Entry<String, Object> entry : parameter.entrySet()) {
            jobDetail.getJobDataMap().put(entry.getKey(),entry.getValue());
        }
        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, triggerGroupName).startAt(startDate)
                .withSchedule(executionRule(interval,timeUnit,count)).build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 添加一个执行一次Simple任务
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @param jobClass
     * @param startDate
     * @throws SchedulerException
     */
    private void createOnceSimpleJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Class<? extends Job> jobClass, Date startDate) throws SchedulerException {
//        if(startDate.getTime() <= System.currentTimeMillis()){
//            throw new IllegalArgumentException("第一次开始时间必须大于当前时间");
//        }
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroupName).build();
        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, triggerGroupName).startAt(startDate)
                .withSchedule(SimpleScheduleBuilder.repeatHourlyForTotalCount(1)).build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 添加一个执行一次带参数Simple任务
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @param jobClass
     * @param startDate
     * @throws SchedulerException
     */
    private void createOnceSimpleJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName, Class<? extends Job> jobClass, Date startDate, Map<String, Object> parameter) throws SchedulerException {
//        if(startDate.getTime() <= System.currentTimeMillis()){
//            throw new IllegalArgumentException("第一次开始时间必须大于当前时间");
//        }
        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroupName).build();
        for (Map.Entry<String, Object> entry : parameter.entrySet()) {
            jobDetail.getJobDataMap().put(entry.getKey(),entry.getValue());
        }
        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, triggerGroupName).startAt(startDate)
                .withSchedule(SimpleScheduleBuilder.repeatHourlyForTotalCount(1)).build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 移除一个任务(使用默认的任务组名，触发器名，触发器组名)
     * @param jobName
     * @throws SchedulerException
     */
    public void removeDefaultJob(String jobName) throws SchedulerException {
        removeJob(jobName,JOB_DEFAULT_GROUP_NAME,jobName,TRIGGER_DEFAULT_GROUP_NAME);
    }

    /**
     * 移除一个任务
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     * @throws SchedulerException
     */
    public void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
        JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
        scheduler.pauseTrigger(triggerKey);
        scheduler.unscheduleJob(triggerKey);
        scheduler.deleteJob(jobKey);
    }

    /**
     * 启动所有定时任务
     * @throws SchedulerException
     */
    public void startAllJobs() throws SchedulerException {
        scheduler.start();
    }

    /**
     * 关闭所有定时任务
     * @throws SchedulerException
     */
    public void shutdownAllJobs() throws SchedulerException {
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    /**
     * 解析执行规则
     * @param interval
     * @param timeUnit
     * @param count
     * @return
     */
    private SimpleScheduleBuilder executionRule(Integer interval, TimeUnit timeUnit, Integer count){
        switch(timeUnit){
            case SECONDS:
                return SimpleScheduleBuilder.repeatSecondlyForTotalCount(count,interval);
            case MINUTES:
                return SimpleScheduleBuilder.repeatMinutelyForTotalCount(count,interval);
            case HOURS:
                return SimpleScheduleBuilder.repeatHourlyForTotalCount(count,interval);
            default:
                throw new NoClassDefFoundError("不支持此单位类型");
        }
    }

}