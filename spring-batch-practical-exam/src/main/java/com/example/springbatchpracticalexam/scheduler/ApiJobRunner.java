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
public class ApiJobRunner extends JobRunner {

    private final Scheduler scheduler;

    @Override
    protected void doRun(ApplicationArguments args) {
        /*
         * JobDetail 은 Schedule 의 Job 의 상세정보를 담는 Class
         */
        JobDetail jobDetail = buildJobDetail(ApiSchJob.class, "apiJob", "batch", new HashMap<>());

        /*
         * Trigger 는 언제 Schedule 을 실행할 건지에 대한 시간 정보를 같는 Class
         */
        Trigger trigger = buildJobTrigger("0/30 * * * * ?");

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
