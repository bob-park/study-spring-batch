package com.example.springbatchjob.ch02_jobbuilderfactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JobBuilderFactory
 *
 * <pre>
 *     - Spring Batch 는 Job 과 Step 을 쉽게 생성 및 설정할 수 있도록 Util 성격의 builder class 들을 제공
 *
 *     - JobBuilderFactory
 *          - JobBuilder 를 생성하는 Factory Class
 *          - JobBuilderFactory.get(String)
 *              - jobName 은 Spring Batch 가 job 을 실행시킬때 참조하는 Job 이름
 *
 *     - JobBuilder
 *          - Job 을 구성하는 설정 조건에 따라 두개의 하위 Builder Class 를 생성하고 실제 Job 생성을 위임
 *          - JobBuilder.start(Step) 시 기본적으로 SimpleJobBuilder 가 SimpleJob 생성
 *          - JobBuilder.start(Flow) or JobBuilder.flow(Step) 시 기본적으로 FlowJobBuilder 가 FlowJob 생성
 *              - FlowJobBuilder -> JobFlowBuilder ->  FlowBuilder 생성 -> Flow 생성
 *
 *          - SimpleJobBuilder
 *              - SimpleJob 을 생성하는 builder class
 *              - Job 실행과 관련된 여러 설정 API 제공
 *
 *          - FlowJobBuilder
 *              - FlowJob 을 생성하는 Builder class
 *              - 내부적으로 FlowBuilder 를 반환, Flow 실행과 관련된 여러 설정 API 제공
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class JobBuilderFactoryConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

//    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .start(step1())
            .next(step2())
            .build();
    }

    @Bean
    public Job flowJob1() {
        return jobBuilderFactory.get("flowJob1")
            .start(flow())
            .next(step5()) // SimpleJob 과 달리 flow 에 따른 복잡도가 존재한다.
            .end() // FlowJob 이 끝날 경우 반드시 end() 를 호출해야함
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

    @Bean
    public Flow flow() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow"); // flow 안에 job 을 포함

        flowBuilder.start(step3())
            .next(step4())
            .end();

        return flowBuilder.build();
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

    @Bean
    public Step step4() {
        return stepBuilderFactory.get("step4")
            .tasklet((contribution, chunkContext) -> {
                log.info("step4 was execute");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step step5() {
        return stepBuilderFactory.get("step5")
            .tasklet((contribution, chunkContext) -> {
                log.info("step5 was execute");
                return RepeatStatus.FINISHED;
            })
            .build();
    }
}
