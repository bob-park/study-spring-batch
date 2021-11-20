package com.example.springbatchchunk.ch01_chunk;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ChunkOrientedTasklet
 *
 * <pre>
 *     - ChunkOrientedTasklet 은 Spring Batch 에서 제공하는 Tasklet 의 구현체로써, Chunk 지향 프로세싱을 담당하는 Domain 객체
 *     - ItemReader, ItemWriter, ItemProcessor 를 사용해 Chunk 기반의 데이터 입출력 처리를 담당한다.
 *     - TaskletStep 에 의해서 반복적으로 실행되며, ChunkOrientedTasklet 이 실행될 때마다 매번 새로운 Transaction 이 생성되어 처리가 이루어진다.
 *     - Exception 발생 시, 해당 Chunk 는 Rollback 되며, 이전에 Commit 한 Chunk 는 완료 상태가 유지된다.
 *     - 내부적으로 ItemReader 를 핸들링하는 ChunkProvider 와 ItemProcessor, ItemWriter 를 핸들링하는 ChunkProcessor 타입의 구현체를 가진다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class ChunkOrientedTaskletConfiguration {

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
            .<String, String>chunk(2) // chunk size 설정, chunk size 는 commit interval 을 의미함, input, output generic type 설정
//            .<String, String>chunk(CompletionPolicy) // Chunk 프로세스를 완료하기 위한 정책 설정 class 지정
            // Source 로부터 item 을 reader 하거나 가져오는 ItemReader 구현체 설정
            .reader(
                new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5")))
            // item 변형, 가공, 필터링 하기 위한 ItemProcessor 구현체 설정
            .processor((ItemProcessor<String, String>) item -> {
                log.info("item={}", item);
                return "my_" + item;
            })
            // item 을 목적지에 쓰거나 보내기 위한 ItemWriter 구현체 설정
            .writer(items -> {
                Thread.sleep(300);
                log.info("items={}", items);
            })
//            .stream(ItemStream) // 재시작 데이터를 관리하는 callback 에 대한 Stream 등록
//            .readerIsTransactionalQueue() // item 이 JMS, Message Queue Server 와 같은 트랜잭션 외부에서 읽혀지고 캐시할 것인지 여부, default: false
//            .listener(ChunkListener) // Chunk Process 가 진행되는 특정 시점에 Callback 제공받도록 ChunkListener 설정
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
