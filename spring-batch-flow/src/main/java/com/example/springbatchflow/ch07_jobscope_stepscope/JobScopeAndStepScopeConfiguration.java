package com.example.springbatchflow.ch07_jobscope_stepscope;

import com.example.springbatchflow.ch07_jobscope_stepscope.listener.CustomJobListener;
import com.example.springbatchflow.ch07_jobscope_stepscope.listener.CustomStepListener;
import javax.batch.api.listener.JobListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JobScope & StepScope
 *
 * <pre>
 *     - Scope
 *          - Spring Container 에서 Bean 이 관리되는 범위
 *          - singleton, prototype, request, session, application 등이 있으며, 기본은 singleton 으로 생성됨
 *
 *     - Spring Batch Scope
 *          - @JobScope, @StepScope
 *              - Job 과 Step 의 Bean 생성과 실행에 관여하는 Scope
 *              - Proxy Mode 를 기본값으로 하는 Scope
 *                  - @Scope(value = "job", proxyMode = ScopedProxyMode.TARGET_CLASS)
 *              - 해당 Scope 가 선언되면 Bean 이 생성이 Application 구동시점이 아닌 Bean 의 실행시점에 이루어진다.
 *                  - @Values 를 주입해서 Bean 의 실행 시점에 값을 참조할 수 있으며, 일종의 Lazy Binding 이 가능해진다.
 *                  - @Value("#{jobParameters[파라미터명]}"), @Value("#{jobExecutionContext[파라미터명]}"), @Value("#{stepExecutionContext[파라미터명]}")
 *                  - @Values 를 사용할 경우 Bean 선언문에 @JobScope, @StepScope 를 정의하지 않으면 오류를 발생하므로 반드시 선언해야함
 *              - Proxy Mode 로 Bean 이 선언되기 때문에, Application 구동시점에는 Bean 의 Proxy 객체가 생성되어 실행 시점에 실제 Bean 을 호출해준다.
 *              - 병렬처리 시 각 Thread 마다 생성된 Scope Bean 이 할당되기 때문에, Thread 에 안전하게 실행이 가능하다.
 *
 *      - @JobScope
 *          - Step 선언문에 정의한다.
 *          - @Value : jobParameter, JobExecutionContext 만 사용가능
 *
 *      - @StepScope
 *          - Tasklet 이나 ItemReader, ItemWriter, itemProcessor 선언문에 정의한다.
 *          - @Value : jobParameter, jobExecutionContext, stepExecutionContext 사용가능
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class JobScopeAndStepScopeConfiguration {

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
