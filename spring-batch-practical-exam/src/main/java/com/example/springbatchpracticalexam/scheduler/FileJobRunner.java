package com.example.springbatchpracticalexam.scheduler;

import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

/**
 * Spring 이 구동되는 시점에 시작하기 위해 ApplicationRunner 를 구현한다.
 */
@Component
@RequiredArgsConstructor
public class FileJobRunner extends JobRunner {

    private final Scheduler scheduler;

    @Override
    protected void doRun(ApplicationArguments args) {

        String[] sourceArgs = args.getSourceArgs();

        JobDetail jobDetail = buildJobDetail(FileSchJob.class, "fileJob", "batch", new HashMap<>());

        Trigger trigger = buildJobTrigger("0/50 * * * * ?");

        // JobDetail 에 데이터 전달.
        jobDetail.getJobDataMap().put("requestDate", sourceArgs[0]);

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
