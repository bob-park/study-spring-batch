package com.example.springbatchexceptionhandle.ch_02_faulttolerant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * FaultTolerant
 *
 * <pre>
 *     - Spring Batch 는 Job 실행 중 오류가 발생할 경우 장애를 처리하기 위한 기능을 제공하며, 이를 통해 복원력을 향상시킬 수 있다.
 *     - 오류가 발생해도 Step 이 즉시 종료되지 않고, Retry 혹은 Skip 기능을 활성화 함으로써 내결함성 서비스가 가능하도록 한다.
 *     - 프로그램의 내결함성을 위해 Skip 과 Retry 기능을 제공한다.
 *          - Skip
 *              - 다음에 적용할 수 있다.
 *                  - ItemReader
 *                  - ItemProcessor
 *                  - ItemWriter
 *
 *          - Retry
 *              - 다음에 적용할 수 있다.
 *                  - ItemProcessor
 *                  - ItemWriter
 *
 *      - FaultTolerant 구조는 Chunk 기반의 프로세스 기반 위에 Skip 과 Retry 기능이 추가되어 재정의 되어 있다.
 *
 *
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class FaultTolerantConfiguration {

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
            .reader(new ItemReader<>() {

                private int i = 0;

                @Override
                public String read()
                    throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

                    i++;

                    if (i == 1) {
                        throw new IllegalArgumentException("This exception is skipped.");
                    }

                    return i > 3 ? null : "item-" + i;
                }
            })
            .processor((ItemProcessor<String, String>) item -> {
                throw new IllegalStateException("This exception is retried");

//                return null;
            })
            .writer(items -> log.info("items={}", items))
            .faultTolerant() // 내결함성 기능 활성화
            .skip(IllegalArgumentException.class) // 예외발생 시 Skip 할 예외 타입 설정
            .skipLimit(2) // Skip 제한 횟수 설정
//            .skipPolicy(skipPolicy) // Skip 을 어떤 조건과 기준으로 적용할 것인지 정책 설정
//            .noSkip(type) // 예외 발생시 Skip 하지 않을 예외 타입 설정
            .retry(IllegalStateException.class) // 예외 발생 시 Retry 할 예외 타입 설정
            .retryLimit(2) // Retry 제한 횟수 설정
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
}
