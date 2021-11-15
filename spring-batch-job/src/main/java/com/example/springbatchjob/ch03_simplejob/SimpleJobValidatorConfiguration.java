package com.example.springbatchjob.ch03_simplejob;

import com.example.springbatchjob.ch03_simplejob.validator.CustomJobParametersValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SimpleJob Validator
 *
 * <pre>
 *      - Job 실행에 꼭 필요한 파라미터를 검증하는 용도
 *      - DefaultJobParametersValidator 구현체를 지원
 *      - 좀 더 복잡한 제약 조건이 있다면, Interface 를 직접 구현할 수 있음
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class SimpleJobValidatorConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .start(step1())
            .next(step2())
            .next(step3())
            // Repository 에서 생성하기 전 실행, job 실행하기 전 실행, 총 2번 실행
//            .validator(new CustomJobParametersValidator()) // Custom
            .validator(new DefaultJobParametersValidator(
                // required
                new String[]{"name", "date"},
                // optional
                new String[]{"count"})) // default
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
    public Step step3() {
        return stepBuilderFactory.get("step3")
            .tasklet((contribution, chunkContext) -> {
                log.info("step3 was execute");
                return RepeatStatus.FINISHED;
            })
            .build();
    }
}
