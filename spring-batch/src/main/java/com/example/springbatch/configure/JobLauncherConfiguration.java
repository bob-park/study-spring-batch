package com.example.springbatch.configure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JobLauncher
 *
 * <pre>
 *     - Batch Job 을 실행시키는 역할
 *     - Job 과 Job Parameters 를 인자로 받으며, 요청된 Batch Job 을 수행 후 최종 Client 에게 JobExecution 를 반환
 *
 *     - Job 실행
 *          - JobLauncher.run(Job, JobParameters)
 *          - Spring boot Batch 에서는 JobLauncherApplicationRunner 가 자동으로 JobLauncher 실행
 *          - 동기적 실행
 *              - taskExecutor 를 SyncTaskExecutor 로 설정할 경우 (default : SyncTaskExecutor)
 *              - JobExecution 을 획득하고 배치 처리를 최종 완료한 이후 Client 에게 JobExecution 을 반환
 *              - Scheduler 에 의한 배치 처리에 적합함 - 배치 처리 시간이 길어도 상관없는 경우
 *          - 비 동기적 실행
 *              - taskExecutor 를 SimpleAsyncTaskExecutor 로 설정한 경우
 *              - JobExecution 을 획득한 후 Client 에게 바로 JobExecution 를 반환하고 배치 처리를 완료 한다.
 *              - HTTP Request 에 의한 배치처리에 적합함 - 배치처리 시간이 길 경우 응답이 늦어지지 않도록 함
 * </pre>
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobLauncherConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
            .start(step1())
            .next(step2())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .tasklet((contribution, chunkContext) -> {
                log.info("step1 was execute");

                Thread.sleep(3_000);

                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet((contribution, chunkContext) -> {

                return null;
            })
            .build();
    }

}
