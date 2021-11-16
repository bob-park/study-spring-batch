package com.example.springbatchstep.ch02_taskletstep;

import com.example.springbatchstep.ch02_taskletstep.tasklet.CustomTasklet;
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
 * TaskletStep - startLimit() / allowStartIfComplete()
 *
 * <pre>
 *     - startLimit()
 *          - Step 의 실행 횟수를 조정할 수 있다.
 *          - Step 마다 설정할 수 있다.
 *          - 설정값을 초과해서 다시 실행하려고 하면, StartLimitExceededException 발생
 *          - start-limit 의 default : Integer.MAX_VALUE
 *
 *     - allowStartIfComplete()
 *          - 재시작 가능한 job 에서 Step 의 이전 성공 여부와 상관없이 항상 Step 을 실행하기 위한 설정
 *          - 실행마다 유효성을 검증하는 Step 이나 사전 작업이 꼭 필요한 Step 등
 *          - 기본적으로 Completed 상태를 가진 Step 은 Job 재시작 시 실행하지 않고 Skip 한다.
 *          - allow-start-if-completed 가 true 로 설정된 step 은 항상 실행
 * </pre>
 *
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class TaskletExtraConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
//            .incrementer(new RunIdIncrementer())
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
            .allowStartIfComplete(true)
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet((contribution, chunkContext) -> {
                log.info("step2 was execute");
                throw new RuntimeException("step2 failed.");
//                return RepeatStatus.FINISHED;
            })
            .startLimit(3)
            .build();
    }

}
