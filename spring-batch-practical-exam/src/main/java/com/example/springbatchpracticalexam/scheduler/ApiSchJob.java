package com.example.springbatchpracticalexam.scheduler;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiSchJob extends QuartzJobBean {

    private final Job apiJob;

    private final JobLauncher jobLauncher;

    @SneakyThrows
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // 실제 Schedule 의 대한 작업을 정의한다.

        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("id", new Date().getTime())
            .toJobParameters();

        jobLauncher.run(apiJob, jobParameters);

    }
}
