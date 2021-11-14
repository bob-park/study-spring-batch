package com.example.springbatch.configure;

import com.example.springbatch.step.executioncontext.ExecutionContextTasklet1;
import com.example.springbatch.step.executioncontext.ExecutionContextTasklet2;
import com.example.springbatch.step.executioncontext.ExecutionContextTasklet3;
import com.example.springbatch.step.executioncontext.ExecutionContextTasklet4;
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
 * ExecutionContext
 *
 * <pre>
 *     - Framework 에서 유지 및 관리하는 key/value (Map) 으로 된 collection 으로 StepExecution 또는 JobExecution 객체의 상태를 저장하는 공유 객체
 *     - DB 에 직렬화된 값으로 저장됨 - { "key": "value" }
 *     - 공유 범위
 *          - Step : StepExecution 에 저장되며 Step 간 서로 공유 X
 *          - Job : 각 Job 의 JobExecution 에 저장, Job 간 서로 공유 X, Job 의 Step 간 서로 공유 O
 *     - Job 재시작시 이미 처리한 Row 데이터는 Skip, 이후 수행되도록 할 떄 상태 정보를 활용한다.
 * </pre>
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ExecutionContextConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final ExecutionContextTasklet1 executionContextTasklet1;
    private final ExecutionContextTasklet2 executionContextTasklet2;
    private final ExecutionContextTasklet3 executionContextTasklet3;
    private final ExecutionContextTasklet4 executionContextTasklet4;

    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
            .start(step1())
            .next(step2())
            .next(step3())
            .next(step4())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .tasklet(executionContextTasklet1)
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet(executionContextTasklet2)
            .build();
    }

    @Bean
    public Step step3() {
        return stepBuilderFactory.get("step3")
            .tasklet(executionContextTasklet3)
            .build();
    }

    @Bean
    public Step step4() {
        return stepBuilderFactory.get("step4")
            .tasklet(executionContextTasklet4)
            .build();
    }

}
