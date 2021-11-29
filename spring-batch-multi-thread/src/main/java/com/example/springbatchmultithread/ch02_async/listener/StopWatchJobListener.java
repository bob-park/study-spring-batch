package com.example.springbatchmultithread.ch02_async.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class StopWatchJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long elapsed = jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime();

        log.info("====================");
        log.info("elapsed={}", elapsed);
        log.info("====================");
    }
}
