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
 * 알아야 할 것
 *
 * <pre>
 *     - JobExecution 은 JobInstance 가 실행될때마다 생성됨
 *     - JobInstance 는 같은 Job + JobParameters 에 의해 1번만 실행되지만, 마지막 JobExecution 의 상태가 COMPLETED 가 아닌 경우 재실행이 가능하다.
 * </pre>
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobExecutionConfiguration {

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
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet((contribution, chunkContext) -> {
                log.info("step2 was execute");
//                throw new IllegalStateException("step2 failed.");
                return RepeatStatus.FINISHED;
            })
            .build();
    }
}
