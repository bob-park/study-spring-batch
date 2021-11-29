package com.example.springbatchmultithread.ch01_basic;

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
 * 기본 개념
 *
 * <pre>
 *      - Single Thread vs Multi Thread
 *          - 프로세스 내 특정 작업을 처리하는 Thread 가 하나일 경우 Single Thread, 여러개일 경우 Multi Thread 로 정의할 수 있다.
 *          - 작업 처리에 있어 Single Thread 와 Multi Thread 의 선택기준은 어떤 방식이 자원을 효율적으로 사용하고 성능처리에 유리한가 하는 점이다.
 *          - 일반적으로 복잡한 처리나 대용량 데이터를 다루는 작업일 경우 전체 소요 시간 및 성능상의 이점을 가져오기 위해 Multi Thread 방식을 선택한다.
 *          - Multi Thread 처리 방식은 데이터 동기화 이슈가 존재하기 떄문에 최대한 고려해서 결정해야 한다.
 *
 *
 *      - Spring Batch Thread Model
 *          - Spring Batch 는 기본적으로 Single Thread 방식으로 작업을 처리한다.
 *          - 성능 향상과 대규모 데이터 작업을 위한 비동기 처리 및 Scale out 기능을 제공한다.
 *          - Local 과 Remote 처리를 지원한다.
 *
 *      - AsyncItemProcessor / AsyncItemWriter
 *          - ItemProcessor 에게 별도의 Thread 가 할당되어 작업을 처리하는 방식
 *
 *      - Multi-threaded Step
 *          - Step 내 chunk 구조인 ItemReader, ItemProcessor, ItemWriter 마다 여러 Thread 가 할당되어 실행하는 방법
 *
 *      - Remote Chunking
 *          - 분산환경처럼 Step 처리가 여러 프로세스로 분할되어 외부의 다른 서버로 전송되어 처리하는 방식
 *
 *      - Parallel Steps
 *          - Step 마다 Thread 가 할당되어 여러개의 Step 을 병렬로 실행하는 방법
 *
 *      - Partitioning
 *          - Master / Slave 방식으로서 Master 가 데이터를 파티셔닝한 다음 각 파티션에게 Thread 를 할당하여 Slave 가 독립적으로 작동하는 방식
 *
 * </pre>
 *
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class BasicConfiguration {

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
            .tasklet((contribution, chunkContext) -> {
                log.info("step1 has executed.");
                return RepeatStatus.FINISHED;
            })
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
