package com.example.springbatchexceptionhandle.ch_04_retry;

import com.example.springbatchexceptionhandle.ch_04_retry.exception.RetryableException;
import com.example.springbatchexceptionhandle.ch_04_retry.processor.RetryItemProcessorV1;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;

/**
 * Retry 2
 *
 * <pre>
 *     -
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class RetryV2Configuration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .incrementer(new RunIdIncrementer())
            .start(step1())
            .next(step2())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .<String, String>chunk(5)
            .reader(itemReader())
            .processor(itemProcessor())
            .writer(items -> log.info("items={}", items))
            .faultTolerant()
            .skip(RetryableException.class)
            .skipLimit(2)
//            .retry(RetryableException.class) // 예외 발생 시 Retry 할 예외 타입 설정
//            .retryLimit(2) // Retry 제한 횟수 설정
            .retryPolicy(retryPolicy()) // Retry 를 어떤 조건과 기준으로 적용할 것인지 정책 설정
//            .backOffPolicy(backOffPolicy) // 다시 Retry 하기 까지의 지연시간 (단위 ms) 설정
//            .noRetry(type) // 예외 발생시 Retry 하지 않을 예외 타입 설정
//            .noRollback(type) // 예외 발생시 Rollback 하지 않을 예외 타입 설정
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet((contribution, chunkContext) -> {
                log.info("step2 has executed.");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public ItemReader<String> itemReader() {
        List<String> items = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            items.add(String.valueOf(i));
        }

        return new ListItemReader<>(items);
    }

    @Bean
    public ItemProcessor<String, String> itemProcessor() {
        return new RetryItemProcessorV1();
    }

    @Bean
    public RetryPolicy retryPolicy() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();

        exceptionClass.put(RetryableException.class, true);

        return new SimpleRetryPolicy(2, exceptionClass);

    }

}
