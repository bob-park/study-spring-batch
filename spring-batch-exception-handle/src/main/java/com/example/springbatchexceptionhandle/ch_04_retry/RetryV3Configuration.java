package com.example.springbatchexceptionhandle.ch_04_retry;

import com.example.springbatchexceptionhandle.ch_04_retry.exception.RetryableException;
import com.example.springbatchexceptionhandle.ch_04_retry.model.Customer;
import com.example.springbatchexceptionhandle.ch_04_retry.processor.RetryItemProcessorV2;
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
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * Retry 3
 *
 * <pre>
 *     -
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class RetryV3Configuration {

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
            .<String, Customer>chunk(5)
            .reader(itemReader())
            .processor(itemProcessor(null))
            .writer(items -> log.info("items={}", items))
            .faultTolerant()
            .skip(RetryableException.class)
            .skipLimit(2)
//            .retry(RetryableException.class) // ?????? ?????? ??? Retry ??? ?????? ?????? ??????
//            .retryLimit(2) // Retry ?????? ?????? ??????
//            .retryPolicy(retryPolicy()) // Retry ??? ?????? ????????? ???????????? ????????? ????????? ?????? ??????
//            .backOffPolicy(backOffPolicy) // ?????? Retry ?????? ????????? ???????????? (?????? ms) ??????
//            .noRetry(type) // ?????? ????????? Retry ?????? ?????? ?????? ?????? ??????
//            .noRollback(type) // ?????? ????????? Rollback ?????? ?????? ?????? ?????? ??????
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
    public ItemProcessor<String, Customer> itemProcessor(RetryTemplate retryTemplate) {
        return new RetryItemProcessorV2(retryTemplate);
    }

    @Bean
    public RetryTemplate retryTemplate() {

        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(RetryableException.class, true);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(2_000);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(2, exceptionClass);

        RetryTemplate retryTemplate = new RetryTemplate();

        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }

}
