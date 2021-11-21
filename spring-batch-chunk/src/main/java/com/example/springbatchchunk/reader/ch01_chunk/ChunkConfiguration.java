package com.example.springbatchchunk.reader.ch01_chunk;

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
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;

/**
 * Chunk
 *
 * <pre>
 *     - Chunk 란 여러개의 Item 을 묶은 하나의 덩어리, 블록을 의미
 *     - 한번에 하나씩 Item 을 입력받아 Chunk 단위의 덩어리로 만든 후 Chunk 단위로 트랜잭션을 처리함, 즉, Chunk 단위의 Commit 과 Rollback 이 이루어짐
 *     - 일반적으로 대용량 데이터를 한번에 처리하는 것이 아닌, Chunk 단위로 쪼개서 더이상 처리할 데이터가 없을 때까지 반복해서 입출력하는데 사용됨
 *
 *     - Chunk<I> vs Chunk<O>
 *         - Chunk<I> 는 ItemReader 로 읽은 하나의 아이템을 Chunk 에서 정한 개수 만큼 반복해서 저장하는 타입
 *         - Chunk<O> 는 ItemReader 로부터 전달받은 Chunk<I> 를 참조해서 ItemProcessor 에서 적절하게 가공, 필터링한 다음 ItemWriter 에 전달하는 타입
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
            .next(step2())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .<String, String>chunk(5)
            // 전체 item 읽기 - data 필터링
            .reader(
                new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5")))
            // 개별 item 처리 - data 가공
            .processor((ItemProcessor<String, String>) item -> {
                log.info("item={}", item);
                return "my " + item;
            })
            // 전체 item 쓰기
            .writer(items -> {
                Thread.sleep(300);
                log.info("items={}", items);
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
