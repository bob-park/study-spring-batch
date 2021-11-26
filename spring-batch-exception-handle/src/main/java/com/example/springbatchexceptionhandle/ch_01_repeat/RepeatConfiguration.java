package com.example.springbatchexceptionhandle.ch_01_repeat;

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
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.batch.repeat.exception.SimpleLimitExceptionHandler;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.context.annotation.Bean;

/**
 * Repeat
 *
 * <pre>
 *     - Spring Batch 는 얼마나 작업을 반복해야 하는지 알려줄 수 있는 기능을 제공한다.
 *     - 특정 조건이 충족될 때까지 (또는 특정 조건이 아직 충족되지 않을때 까지) Job 또는 Step 을 반복하도록 Batch Application 을 구성할 수 있다.
 *     - Spring Batch 에서는 Step 의 반복과 Chunk 반복을 RepeatOperation 을 사용해서 처리하고 있다.
 *     - 기본 구현체로 RepeatTemplate 를 제공한다.
 *
 *     - RepeatStatus
 *          - Spring Batch 의 처리가 끝났는지 판별하기 위한 열거형(enum)
 *              - CONTINUABLE - 작업이 남아있음
 *              - FINISHED - 더이상 반복 없음
 *
 *      - CompletionPolicy
 *          - RepeatTemplate 의 iterate 메소드 안에서 반복을 중단할 지 결정
 *          - 실행 횟수 또는 완료시기, 오류 발생시 수행 할 작업에 대한 반복여부 결정
 *          - 정상종료을 알리는데 사용된다.
 *
 *      - ExceptionHandler
 *          - RepeatCallback 안에서 예외가 발생하면 RepeatTemplate 가 ExceptionHandler 를 참조해서 예외를 다시 던질지 여부 겳정
 *          - 예외를 받아서 다시 던지게 되면 반복 종료
 *          - 비정상 종료를 알리는데 사용된다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class RepeatConfiguration {

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

                    return i > 3 ? null : "item-" + i;
                }
            })
            .processor(new ItemProcessor<>() {

                RepeatTemplate repeatTemplate = new RepeatTemplate();

                @Override
                public String process(String item) throws Exception {

                    /*
                    ! 마지막에 추가된 CompletionPolicy 만 적용
                    */
                    /* 가장 기본이 되는 CompletionPolicy */
                    // chunk 3 
//                    repeatTemplate.setCompletionPolicy(new SimpleCompletionPolicy(3));
                    /* TimeoutTerminationPolicy */
//                    repeatTemplate.setCompletionPolicy(new TimeoutTerminationPolicy(3_000));

                    /* 여러개의 CompletionPolicy 적용*/
                    // OR 조건
                    // 설정한 CompletionPolicy 중 하나라도 부합하다면, 반복을 종료한다.
//                    CompositeCompletionPolicy completionPolicy = new CompositeCompletionPolicy();
//                    CompletionPolicy[] completionPolicies = new CompletionPolicy[]{
//                        new SimpleCompletionPolicy(3),
//                        new TimeoutTerminationPolicy(3_000)
//                    };
//
//                    completionPolicy.setPolicies(completionPolicies);
//
//                    repeatTemplate.setCompletionPolicy(completionPolicy);

                    /* ExceptionHandler */
                    /* 가장 기본이 되는 ExceptionHandler */
                    // 예외가 발생해도 해당 수 만큼 예외를 통과시킨다.
                    repeatTemplate.setExceptionHandler(simpleLimitExceptionHandler());

                    // item 마다 무한 반복이지만, CompletionPolicy 에 의해 반복을 종료한다.
                    repeatTemplate.iterate(context -> {

                        log.info("repeatTemplate is testing.");

                        throw new RuntimeException("Exception is occurred.");

//                        return RepeatStatus.CONTINUABLE;
                    });

                    return item;
                }
            })
            .writer(items -> log.info("items={}", items))
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
    public ExceptionHandler simpleLimitExceptionHandler(){
        return new SimpleLimitExceptionHandler(3);
    }
}


