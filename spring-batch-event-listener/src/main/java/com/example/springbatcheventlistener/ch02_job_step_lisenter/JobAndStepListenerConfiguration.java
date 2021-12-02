package com.example.springbatcheventlistener.ch02_job_step_lisenter;

import com.example.springbatcheventlistener.ch02_job_step_lisenter.listener.CustomJobExecutionListener;
import com.example.springbatcheventlistener.ch02_job_step_lisenter.listener.CustomStepExecutionListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JobExecutionListener
 *
 * <pre>
 *     - Job 의 성공여부와 상관없이 호출된다.
 *     - 성공/실패 여부는 JobExecution 을 통해 알 수 있다.
 * </pre>
 *
 * <p>
 * StepExecutionListener
 *
 * <pre>
 *     - Step 의 성공여부와 상관없이 호출된다.
 *     - 성공 / 실패 여부는 StepExecution 을 통해 알 수 있다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class JobAndStepListenerConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .incrementer(new RunIdIncrementer())
            .start(step1())
            .next(step2())
            .listener(new CustomJobExecutionListener())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .tasklet((contribution, chunkContext) -> {
                log.info("step1 has executed.");
                return RepeatStatus.FINISHED;
            })
            .listener(new CustomStepExecutionListener())
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet((contribution, chunkContext) -> {
                log.info("step2 has executed.");
                return RepeatStatus.FINISHED;
            })
            .build();
    }
}
