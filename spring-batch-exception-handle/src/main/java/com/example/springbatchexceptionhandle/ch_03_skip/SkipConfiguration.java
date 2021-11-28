package com.example.springbatchexceptionhandle.ch_03_skip;

import com.example.springbatchexceptionhandle.ch_03_skip.exception.NoSkippableException;
import com.example.springbatchexceptionhandle.ch_03_skip.exception.SkippableException;
import com.example.springbatchexceptionhandle.ch_03_skip.processor.SkipItemProcessor;
import com.example.springbatchexceptionhandle.ch_03_skip.writer.SkipItemWriter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.LimitCheckingItemSkipPolicy;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Skip
 *
 * <pre>
 *     - Skip 은 데이터를 처리하는 동안 설정된 Exception 이 발생했을 경우, 해당 데이터 처리를 건너뛰는 기능이다.
 *     - 데이터의 사소한 오류에 대해 Step 의 실패처리 대신 Skip 을 함으로써, 배치 수행의 빈번한 실패를 줄일 수 있게 한다.
 *
 *
 *     - Skip 기능은 내부적으로 SkipPolicy 를 통해서 구현되어 있다.
 *     - Skip 가능 여부를 판별하는 기준은 다음과 같다.
 *          - Skip 대상에 포함된 예외인지 여부
 *          - Skip 카운터를 초과 했는지 여부
 *
 *     - Skip 정책에 따라 Item 의 Skip 여부를 판단하는 클래스
 *     - Spring Batch 가 기본적으로 제공하는 SkipPolicy 구현체들이 있으며, 필요시 직접 생성해서 사용할 수 있다. 그리고 내부적으로 Classifier 클래스들을 활용하고 있다.
 *          - AlwaysSkipItemSkipPolicy : 항상 Skip 한다.
 *          - ExceptionClassifierSkipPolicy : 예외대상을 분류하여 Skip 여부를 결정한다.
 *          - CompositeSkipPolicy : 여러 SkipPolicy 를 탐색하면서 Skip 여부를 결정한다.
 *          - LimitCheckingItemSkipPolicy : Skip 카운터 및 예외 등록 결과에 따라 Skip 여부를 결정한다. default 로 설정된다.
 *          - NeverSkipItemSkipPolicy : Skip 하지 않는다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class SkipConfiguration {

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
                public String read() throws NoSkippableException {

                    i++;

                    if (i == 3) {
                        throw new NoSkippableException("skip");
                    }

                    log.info("ItemReader={}", i);

                    return i > 20 ? null : String.valueOf(i);
                }
            })
            .processor(itemProcessor())
            .writer(itemWriter())
            .faultTolerant()
//            .skip(SkippableException.class) // 예외발생 시 Skip 할 예외 타입 설정
//            .skipLimit(2) // Skip 제한 횟수 설정
//            .skipPolicy(limitCheckingItemSkipPolicy()) // Skip 을 어떤 조건과 기준으로 적용할 것인지 정책 설정
//            .skipPolicy(new AlwaysSkipItemSkipPolicy()) // 항상 Skip
//            .skipPolicy(new NeverSkipItemSkipPolicy()) // 항상 No Skip
            .noSkip(NoSkippableException.class) // 예외 발생시 Skip 하지 않을 예외 타입 설정
            .build();
    }

    @Bean
    public SkipPolicy limitCheckingItemSkipPolicy() {

        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();

        exceptionClass.put(SkippableException.class, true);

        return new LimitCheckingItemSkipPolicy(2, exceptionClass);
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
    public ItemWriter<String> itemWriter() {
        return new SkipItemWriter();
    }

    @Bean
    public ItemProcessor<String, String> itemProcessor() {
        return new SkipItemProcessor();
    }
}
