package com.example.springbatchpracticalexam.scheduler;

import java.util.Map;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * Spring 이 구동되는 시점에 시작하기 위해 ApplicationRunner 를 구현한다.
 */
public abstract class JobRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {

        doRun(args);
    }

    protected abstract void doRun(ApplicationArguments args);

    public Trigger buildJobTrigger(String scheduleExp) {
        return TriggerBuilder.newTrigger()
            .withSchedule(CronScheduleBuilder.cronSchedule(scheduleExp)).build();
    }

    public JobDetail buildJobDetail(Class<? extends Job> job, String name, String group,
        Map<? extends String, ?> params) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAll(params);

        return JobBuilder.newJob(job).withIdentity(name, group).usingJobData(jobDataMap).build();

    }
}
