package com.example.springbatchjob.ch03_simplejob;

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
 * SimpleJob preventRestart()
 *
 * <pre>
 *     - Job 의 재시작 여부를 설정
 *     - Job 이 실패해도 재시작이 안되며, Job 을 재시작하려고 하면, JobRestartException 발생
 *     - 재시작과 과련 있는 기능으로 Job 을 처음 실행하는 것과는 아무런 상관 없음
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class PreventRestartConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .start(step1())
            .next(step2())
            .next(step3())
            .preventRestart() // 재시작하지 않도록 설정
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
//                throw new RuntimeException("step2 was failed.");

                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step step3() {
        return stepBuilderFactory.get("step3")
            .tasklet((contribution, chunkContext) -> {
                log.info("step3 was execute");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

}
