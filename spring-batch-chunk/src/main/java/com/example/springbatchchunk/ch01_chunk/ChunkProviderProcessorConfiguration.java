package com.example.springbatchchunk.ch01_chunk;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ChunkOrientedTasklet - ChunkProvider / ChunkProcessor
 *
 * <pre>
 *     - ChunkProvider
 *          - ItemReader 를 사용해서 Source 로부터 Item 을 Chunk Size 만큼 읽어서 Chunk 단위로 만들어 제공하는 도매인 객체
 *          - Chunk<I> 를 만들고 내부적으로 반복문을 사용해서 ItemReader.read() 를 계속 호출하면서 Item 을 Chunk 에 쌓는다.
 *          - 외부로부터 ChunkProvider 가 호출될 때마다 항상 새로운 Chunk 가 생성된다.
 *          - 반복문 종료 시점
 *              - Chunk Size 만큼 item 을 읽어서 반복문 종료되고 ChunkProcessor 로 넘어감
 *              - itemReader 가 읽은 item 이 null 일 경우 반복문 종료 및 해당 Step 반복문까지 종료
 *          - 기본 구현체로서 SimpleChunkProvider 와 FaultTolerantChunkProvider 가 있다.
 *
 *     - ChunkProcessor
 *          - ItemProcessor 를 사용해서 Item 을 변형, 가공, 필터링하고 ItemWriter 를 사용해서 Chunk 데이터를 저장 출력한다.
 *          - Chunk<O> 를 만들고 앞에서 넘어온 Chunk<I> 의 Item 을 한 건씩 처리한 후 Chunk<O> 에 저장한다.
 *          - 외부로부터 ChunkProcessor 가 호출될때마다 항상 새로운 Chunk 가 생성된다.
 *          - ItemProcessor 는 설정 시 선택하상으로서 만약 객체가 존재하지 않을 경우 ItemReader 에서 읽은 item 그대로가 Chunk<O> 에 저장된다.
 *          - ItemProcessor 처리가 완료되면 Chunk<O> 에 있는 List<Item> 을 ItemWriter 에게 전달한다.
 *          - ItemWriter 처리가 완료되면, Chunk Transaction 이 종료하게 되고, Step 반복문에서 ChunkOrientedTask 가 새롭게 실행된다.
 *          - 기본 구현체로써 SimpleChunkProcessor 와 FaultTolerantChunkProcessor 가 있다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class ChunkProviderProcessorConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .start(step1())
            .next(step2())
            .build();
    }

    @Bean
    @JobScope
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .<String, String>chunk(2)
            .reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3")))
            .processor((ItemProcessor<? super String, String>) item -> {
                log.info("item={}", item);
                return "my_" + item;
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

}
