package com.example.springbatcheventlistener.ch04_skip_retry;

import com.example.springbatcheventlistener.ch04_skip_retry.exception.CustomRetryException;
import com.example.springbatcheventlistener.ch04_skip_retry.exception.CustomSkipException;
import com.example.springbatcheventlistener.ch04_skip_retry.listener.CustomRetryListener;
import com.example.springbatcheventlistener.ch04_skip_retry.listener.CustomSkipListener;
import com.example.springbatcheventlistener.ch04_skip_retry.retry.CustomItemProcessor;
import com.example.springbatcheventlistener.ch04_skip_retry.retry.CustomItemWriter;
import java.util.Arrays;
import java.util.List;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Skip & Retry
 *
 * <pre>
 *     - 모두 Annotation 방식을 지원한다.
 *
 *     - SkipListener
 *          - void onSkipRead(Throwable t)
 *              - read 수행중 Skip 이 발생할 경우 호출
 *          - void onSkipWriter(Throwable t)
 *              - write 수행중 Skip 이 발생할 경우 호출
 *          - void onSkipInProcess(T item, Throwable t)
 *              - process 수행중 Skip 이 발생할 경우 호출
 *
 *     - RetryLister
 *          - boolean open(RetryContext context, RetryCallback<T, E> callback)
 *              - 재시도 전 매번 호출, false 를 반환할 경우 retry 를 시도하지 않음
 *
 *          - void close(RetryContext context, RetryCallback<T, E> callback), Throwable t)
 *              - 재시도 후 매번 호출
 *
 *          - void onError(RetryContext context, RetryCallback<T, E> callback), Throwable t)
 *              - 재시도 실패시마다 호출
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class SkipRetryConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .incrementer(new RunIdIncrementer())
            .start(step1())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .<Integer, String>chunk(3)
            .reader(listItemReader())
//            .processor((ItemProcessor<Integer, String>) item -> {
//
//                if (item == 4) {
//                    throw new CustomSkipException("process skipped.");
//                }
//
//                return "item-" + item;
//            })
//            .writer(items -> {
//
//                for (String item : items) {
//                    if (item.equals("item-5")) {
//                        throw new CustomSkipException("write skipped.");
//                    }
//                }
//
//                log.info("items={}", items);
//            })
            .processor(new CustomItemProcessor())
            .writer(new CustomItemWriter())
            .faultTolerant()
//            .skip(CustomSkipException.class)
//            .skipLimit(2)
//            .listener(new CustomSkipListener())
            .retry(CustomRetryException.class)
            .retryLimit(2)
            .listener(new CustomRetryListener())
            .build();
    }

    @Bean
    public ItemReader<Integer> listItemReader() {

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        return new ListItemReader<>(list);
    }
}
