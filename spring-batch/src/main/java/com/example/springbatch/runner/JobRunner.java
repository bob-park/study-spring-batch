package com.example.springbatch.runner;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * ! 주의사항
 *
 * <pre>
 *      - 같은 Job + Job Parameters 가 같은 경우 같은 Job Instance 가 반환된다.
 *      - 같은 Job + Job Parameters 로 실행된(JobExecution 의 Status 가 Completed 인 경우) Job Instance 는 다시 실행되지 않는다.
 * </pre>
 */
//@Component
@RequiredArgsConstructor
public class JobRunner implements ApplicationRunner {

    private final JobLauncher jobLauncher;
    private final Job job;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
            .addString("name", "user1")
            .toJobParameters();

        jobLauncher.run(job, jobParameters);
    }
}
