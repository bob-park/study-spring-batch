package com.example.springbatchflow.ch07_jobscope_stepscope;

import com.example.springbatchflow.ch07_jobscope_stepscope.listener.CustomJobListener;
import com.example.springbatchflow.ch07_jobscope_stepscope.listener.CustomStepListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JobScope & StepScope - Architecture
 *
 * <pre>
 *     - Proxy 객체 생성
 *          - @JobScope, @StepScope 가 붙은 Bean 선언은 내부적으로 Bean 의 Proxy 객체가 생성
 *              - @JobScope
 *                  - @Scope(value = "job", proxyMode = ScopedProxyMode.TARGET_CLASS)
 *              - @StepScope
 *                  - @Scope(value = "step", proxyMode = ScopedProxyMode.TARGET_CLASS)
 *
 *          - Job 실행 시 Proxy 객체가 실제 Bean 을 호출해서 해당 메서드를 실행시키는 구조
 *
 *      - JobScope, StepScope
 *          - Proxy 객체의 실제 대상이 되는 Bean 을 등록, 해제하는 역할
 *          - 실제 Bean 을 저장하고 있는 JobContext, StepContext 를 가지고 있다.
 *
 *      - JobContext, StepContext
 *          - Spring Container 에서 생성된 Bean 을 저장하는 Context 역할
 *          - Job 의 실행시점에서 Proxy 객체가 실제 Bean 을 참조할 때 사용됨
 *
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class JobScopeAndStepScopeArchitectureConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .incrementer(new RunIdIncrementer())
            .start(step1(null))
            .next(step2())
            .listener(new CustomJobListener())
            .build();
    }

    @Bean
    @JobScope
    public Step step1(@Value("#{jobParameters['message']}") String message) {

        log.info("message={}", message);

        return stepBuilderFactory.get("step1")
            .tasklet(tasklet1(null))
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet(tasklet2(null))
            .listener(new CustomStepListener())
            .build();
    }

    @Bean
    @StepScope
    public Tasklet tasklet1(@Value("#{jobExecutionContext['name']}") String name) {

        log.info("name={}", name);

        return (contribution, chunkContext) -> {
            log.info(" >> tasklet1 has executed.");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    @StepScope
    public Tasklet tasklet2(@Value("#{stepExecutionContext['name2']}") String name2) {

        log.info("name2={}", name2);

        return (contribution, chunkContext) -> {
            log.info(" >> tasklet2 has executed.");
            return RepeatStatus.FINISHED;
        };
    }
}
