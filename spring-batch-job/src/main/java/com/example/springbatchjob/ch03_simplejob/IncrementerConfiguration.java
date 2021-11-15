package com.example.springbatchjob.ch03_simplejob;

import com.example.springbatchjob.ch03_simplejob.incrementer.CustomJobParametersIncrementer;
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
 * SimpleJob incrementer()
 *
 * <pre>
 *      - JobParameters 에서 필요한 값을 증가시켜서 다음에 사용될 JobParameters Object 를 return
 *      - 기존의 JobParameter 변경없이 Job 을 여러번 시작하고자 할떄
 *      - RunIdIncrementer 구현체를 지원하여, Interface 를 직접 구현할 수 있음
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class IncrementerConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .start(step1())
            .next(step2())
            .next(step3())
//            .incrementer(new CustomJobParametersIncrementer()) // custom
            .incrementer(new RunIdIncrementer()) // default
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
