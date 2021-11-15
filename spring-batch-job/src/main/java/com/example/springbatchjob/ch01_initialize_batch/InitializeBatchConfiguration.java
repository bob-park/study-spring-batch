package com.example.springbatchjob.ch01_initialize_batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Initialize Batch
 *
 * <pre>
 *     - JobLauncherApplicationRunner
 *          - Spring Batch 작업을 시작하는 ApplicationRunner 로서 batchAutoConfiguration 에서 생성
 *          - Spring boot 에서 제공하는 ApplicationRunner 의 구현체
 *          - Application 이 정상적으로 구동되자 마자 실행
 *          - 기본적으로 Bean 으로 등록된 모든 Job 을 실행
 *
 *     - BatchProperties
 *          - Spring Batch 의 환경설정 Class
 *          - Job Name, Initialize Schema 설정, Table Prefix 등 값을 설정
 *
 *     - Job 실행 옵션
 *          - 지정한 Batch Job 만 실행하도록 할 수 있음
 *          - spring.batch.job.names: ${job.name:NONE}  => Application 실행 시 Program Arguments 로 Job 이름을 입력
 *          - 하나 이상인 경우 ,(콤마) 로 구분하여 입력
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class InitializeBatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job1() {
        return jobBuilderFactory.get("job1")
            .start(step1())
            .next(step2())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .tasklet((contribution, chunkContext) -> {
                log.info("step1 was execute");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet((contribution, chunkContext) -> {
                log.info("step2 was execute");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

}
