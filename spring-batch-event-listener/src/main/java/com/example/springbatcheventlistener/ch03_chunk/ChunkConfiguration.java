package com.example.springbatcheventlistener.ch03_chunk;

import com.example.springbatcheventlistener.ch03_chunk.listener.CustomChunkListener;
import com.example.springbatcheventlistener.ch03_chunk.listener.CustomItemProcessListener;
import com.example.springbatcheventlistener.ch03_chunk.listener.CustomItemReadListener;
import com.example.springbatcheventlistener.ch03_chunk.listener.CustomItemWriterListener;
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
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Chunk
 *
 * <pre>
 *     - 모두 Annotation 방식을 지원한다.
 *
 *     - ChunkListener
 *          - void beforeChunk()
 *              - Transaction 이 시작되기전 호출
 *              - ItemReader 의 read() 메소드를 호출하기 전이다.
 *          - void afterChunk()
 *              - Chunk 가 commit 된 후 호출
 *              - ItemWriter 의 write() 메소드를 호출한 후이다.
 *              - Rollback 되었다면 호출되지 않는다.
 *
 *          - void afterChunkError()
 *              - 오류 발생 및 Rollback 이 되면 호출
 *
 *     - ItemReadListener
 *          - void beforeRead()
 *              - read() 메소드를 호출하기 전 매번 호출
 *
 *          - void afterRead(T item)
 *              - read() 메소드를 호출이 성공할 때마다 호출
 *
 *          - void onReadError(Exception ex)
 *              - onReadError() 는 읽는 도중 오류가 발생하면 호출
 *
 *
 *     - ItemProcessListener
 *          - ItemReadListener 와 비슷하다.
 *
 *
 *     - ItemWriteListener
 *          - ItemReadListener 와 비슷하다.
 *
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class ChunkConfiguration {

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
            .listener(new CustomChunkListener())
            .reader(listItemReader())
            .listener(new CustomItemReadListener())
            .processor((ItemProcessor<Integer, String>) item -> "item-" + item)
            .listener(new CustomItemProcessListener())
            .writer(items -> log.info("items={}", items))
            .listener(new CustomItemWriterListener())
            .build();
    }

    @Bean
    public ItemReader<Integer> listItemReader() {

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        return new ListItemReader<>(list);
    }
}
