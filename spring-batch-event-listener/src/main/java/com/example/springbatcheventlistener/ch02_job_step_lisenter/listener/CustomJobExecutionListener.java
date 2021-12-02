package com.example.springbatcheventlistener.ch02_job_step_lisenter.listener;

import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

@Slf4j
//public class CustomJobExecutionListener implements JobExecutionListener {
public class CustomJobExecutionListener  {

//    @Override
    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job is started.");
        log.info("jobName={}", jobExecution.getJobInstance().getJobName());
    }

//    @Override
    @AfterJob
    public void afterJob(JobExecution jobExecution) {

        long startTime = jobExecution.getStartTime().getTime();

        long endTime = jobExecution.getEndTime().getTime();

        log.info("총 소요시간 : {} ms", (endTime - startTime));
    }
}
