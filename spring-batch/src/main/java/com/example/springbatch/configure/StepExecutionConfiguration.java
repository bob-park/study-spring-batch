package com.example.springbatch.configure;

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
 * StepExecution
 *
 * <pre>
 *     - Step 에 대한 한번의 시도
 *     - Step 의 실행 정보를 갖는다.
 *     - Step 이 매번 시도될 떄마다 생성되며, 각 Step 별로 생성됨
 *     - Job 이 재시작 하더라고 이미 성공적으로 완료된 Step 은 재 실행되지 않고, 실패한 Step 만 실행된다.
 *     - Step 이 실제로 시작됬을 때만, StepExecution 이 생성된다.
 *     
 *     - StepExecution 상태가 모두 정상적으로 완료되야 JobExecution 이 완료 된다.
 *     - Step 의 StepExecution 중 하나라도 실패되면, JobExecution 은 실패된다.
 * </pre>
 *
 *
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class StepExecutionConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
            .start(step1())
            .next(step2())
            .next(step3())
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
//                throw new RuntimeException("step2 has failed.");
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
