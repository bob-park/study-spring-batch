package com.example.springbatchflow.ch06_flowstep;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * FlowStep
 *
 * <pre>
 *  - Step 내에 Flow 를 할당하여 실행시키는 도메인 객체
 *  - FlowStep 의 BatchStatus 와 ExitStatus 은 Flow 의 최종 상태값에 따라 결정된다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class FlowStepConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .incrementer(new RunIdIncrementer())
            .start(flowStep1())
            .next(step3())
            .build();
    }

    @Bean
    public Step flowStep1() {
        return stepBuilderFactory.get("flowStep1")
            .flow(flow1()) // Step 내에서 실행 될 Flow 설정, FlowStepBuilder 반환
            .build(); // FlowStep 객체 생성
    }

    @Bean
    public Flow flow1() {
        FlowBuilder<Flow> builder = new FlowBuilder<>("flow1");
        builder.start(step1())
            .next(step2())
            .end();

        return builder.build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .tasklet((contribution, chunkContext) -> {
                log.info(" >> step1 was executed");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet((contribution, chunkContext) -> {
                log.info(" >> step2 was executed");
                throw new RuntimeException("step2 was executed.");
//                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step step3() {
        return stepBuilderFactory.get("step3")
            .tasklet((contribution, chunkContext) -> {
                log.info(" >> step3 was executed");
                return RepeatStatus.FINISHED;
            })
            .build();
    }


}
