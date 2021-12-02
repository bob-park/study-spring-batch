package com.example.springbatcheventlistener.ch01_basic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 기본 개념
 *
 * <pre>
 *     - Listener 는 Batch 흐름 중에 Job, Step, Chunk 단계의 실행 전후에 발생하는 이벤트를 받아 용도에 맞게 활용할 수 있도록 제공하는 인터셉터 개념의 클래스
 *     - 각 단계별로 로그기록을 남기거나, 소요된 시간을 계산하거나 실행상태 정보들을 참조 및 조회 할 수 있다.
 *     - 이벤트를 닥기 위해서는 Listener 를 등록해야하며 등록은 API 설정에서 각 단계별로 지정할 수 있다.
 *
 *     - Listener
 *          - Job
 *              - JobExecutionListener - Job 실행 전후
 *          - Step
 *              - StepExecutionListener - Step 실행 전후
 *              - ChunkListener - Chunk 실행 전 후 (Tasklet 실행 전후), 오류 시점
 *              - ItemReadListener - ItemReader 실행 전후, 오류시점, item 이 null 일 경우 호출 안됨
 *              - ItemProcessListener - ItemProcessor 실행 전후, 오류시점, item 이 null 일 경우 호출 안됨
 *              - ItemWriteListener - ItemWriter 실행 전후, 오류시점, item 이 null 일 경우 호출 안됨
 *
 *          - SkipListener - 읽기, 쓰기, 처리 Skip 실행 시점, Item 처리가 Skip 될 경우 Skip 된 Item 을 추적함
 *          - RetryListener - Retry 시작, 종료, 에러 시점
 *
 *      - 구현 방법
 *          - Annotation
 *              - Interface 를 구현할 필요가 없다.
 *              - Class 및 Method 명을 자유롭게 작성할 수 있다.
 *              - Object 타입의 Listener 로 설정하기 위해서는 Annotation 방식으로 구현해야 한다.
 *          - Interface
 *
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class BasicConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .start(step1())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .tasklet((contribution, chunkContext) -> {
                log.info("step1 has executed.");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

}
