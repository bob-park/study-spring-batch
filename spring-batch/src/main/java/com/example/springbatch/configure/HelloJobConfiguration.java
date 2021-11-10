package com.example.springbatch.configure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class HelloJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job helloJob() {
        return jobBuilderFactory.get("helloJob")
            .start(helloStep1()) // 시작 step
            .next(helloStep2()) // start 후 다음 실행할 step
            .build();
    }

    @Bean
    public Step helloStep1() {
        return stepBuilderFactory.get("helloStep1")
            .tasklet((contribution, chunkContext) -> {
                log.info(" ======================= ");
                log.info(" >> Hello Spring Batch!! ");
                log.info(" ======================= ");
                return RepeatStatus.FINISHED; // ! null or FINISHED 은 한번 실행 후 종료
            }).build();
    }

    @Bean
    public Step helloStep2() {
        return stepBuilderFactory.get("helloStep2")
            .tasklet((contribution, chunkContext) -> {
                log.info(" ======================= ");
                log.info(" >> step2 was execute ");
                log.info(" ======================= ");
                return RepeatStatus.FINISHED;
            }).build();
    }

}
