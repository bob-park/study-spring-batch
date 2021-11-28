package com.example.springbatchexceptionhandle.ch_04_retry;

import com.example.springbatchexceptionhandle.ch_03_skip.exception.NoSkippableException;
import com.example.springbatchexceptionhandle.ch_04_retry.exception.RetryableException;
import com.example.springbatchexceptionhandle.ch_04_retry.processor.RetryItemProcessor;
import java.util.ArrayList;
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
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Retry
 *
 * <pre>
 *     - Retry 는 ItemProcessor, ItemWriter 에서 설정된 exception 이 발생했을 경우, 지정된 정책에 따라 데이터 처리를 재시도하는 기능이다.
 *     - Skip 과 마찬가지로 Retry 함으로써, 배치 수행의 빈번한 실패를 줄일 수 있게 한다.
 *
 *     - 오류 발생 시 재시도 설정에 의해서 Chunk 단계의 처음부터 다시 시작한다.
 *     - 아이템은 ItemReader 에서 Cache 로 저장한 값을 사용한다.
 *
 *     - Retry 기능은 내부적으로 RetryPolicy 를 통해서 구현되어 있다.
 *     - Retry 가능 여부를 판별하는 기준은 다음과 같다.
 *          - 재시도 대상에 포함된 예외인지
 *          - 재시도 카운터를 초과 했는지
 *
 *     - RetryPolicy
 *          - 재시도 정책에 따라 아이템의 Retry 여부를 판단하는 클래스
 *          - 기본적으로 제공하는 RetryPolicy 구현체들이 있으며, 필요시 직접 생성해서 사용할 수 있다.
 *
 *          - AlwaysRetryPolicy : 항상 재시도를 허용
 *          - ExceptionClassifierRetryPolicy : 예외대상을 분류하여 재시도 여부를 결정한다.
 *          - CompositeRetryPolicy : 여러 RetryPolicy 를 탐색하면서 재시도 여부를 결정한다.
 *          - SimpleRetryPolicy : 재시도 횟수 및 예외 등록 결과에 따라 재시도 여부를 결정한다. default
 *          - MaxAttemptsRetryPolicy : 재시도 횟수에 따라 재시도 여부를 결정한다.
 *          - TimeoutRetryPolicy : 주어진 시간동안 재시도를 허용한다.
 *          - NeverRetryPolicy : 최초 한번만 허용하고 그 이후로는 허용하지 않는다.
 *
 *
 *     - RetryPolicy 와 BackOffPolicy 를 사용해서 재시도 정책을 설정한다.
 *     - BackOffPolicy
 *          - 다시 재시도하기까지의 지연시간(ms) 를 설정
 *          - 처리시간이 긴 데이터가 있을 경우 BackOffPolicy 로 재시도 시간 간격을 조절할 수 있음
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class RetryV1Configuration {

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
            .retry(RetryableException.class) // 예외 발생 시 Retry 할 예외 타입 설정
            .retryLimit(3) // Retry 제한 횟수 설정
//            .retryPolicy(retryPolicy) // Retry 를 어떤 조건과 기준으로 적용할 것인지 정책 설정
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
    public ItemProcessor<String, String> itemProcessor(){
        return new RetryItemProcessor();
    }
}
