package com.example.springbatchstep.ch01_stepbuilderfactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * StepBuilderFactory
 *
 * <pre>
 *     - StepBuilder 를 생성하는 Factory class 로서 get(String) 메서드 제공
 *     - StepBuilderFactory.get(String) - stepName 으로 Step 을 생성
 *
 *     - StepBuilder
 *          - Step 을 구성하는 설정 조건에 따라 5개의 하위 Builder class 를 생성하고 실제 Step 생성을 위임
 *
 *          - TaskletStepBuilder
 *              - TaskletStep 을 생성하는 기본 builder class
 *          - SimpleStepBuilder
 *              - TaskletStep 을 생성하며, 내부적으로 Chunk 기반의 자업을 처리하는 ChunkOrientedTasklet class 생성
 *          - PartitionStepBuilder
 *              - PartitionStep 을 생성하며, multi-thread 방식으로 job 을 실행
 *          - JobStepBuilder
 *              - JobStep 을 생성하며, Step 안에서 Job 을 실행
 *          - FlowStepBuilder
 *              - FlowStep 을 생성하며, Step 안에서 Flow 를 실행
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class StepBuilderFactoryConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .incrementer(new RunIdIncrementer())
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
            .chunk(3)
            .reader(() -> null)
            .processor((ItemProcessor<Object, Object>) item -> null)
            .writer(items -> {

            })
            .build();
    }

    @Bean
    public Step step3() {
        return stepBuilderFactory.get("step3")
            .partitioner(step1())
            .gridSize(2)
            .build();
    }

    @Bean
    public Step step4() {
        return stepBuilderFactory.get("step4")
            .job(job())
            .build();
    }

    @Bean
    public Step step5() {
        return stepBuilderFactory.get("step5")
            .flow(flow())
            .build();
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
            .start(step1())
            .next(step2())
            .next(step3())
            .build();
    }

    @Bean
    public Flow flow() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow");
        flowBuilder.start(step2()).end();

        return flowBuilder.build();

    }
}
