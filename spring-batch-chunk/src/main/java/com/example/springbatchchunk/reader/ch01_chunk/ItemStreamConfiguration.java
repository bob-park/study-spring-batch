package com.example.springbatchchunk.reader.ch01_chunk;

import com.example.springbatchchunk.reader.ch01_chunk.reader.CustomItemReaderV2;
import com.example.springbatchchunk.reader.ch01_chunk.writer.CustomItemWriterV2;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;

/**
 * ItemStream
 *
 * <pre>
 *     - ItemReader 와 ItemWriter 처리 과정 중 상태를 저장하고, 오류가 발생하면 해당 상태를 참조하여 실패한 곳에서 재시작하도록 지원
 *     - Resource 를 open/close 하며 입출력 장치 초기화 등의 작업을 해야하는 경우
 *     - ExecutionContext 를 매개변수로 받아서 상태 정보를 Update 한다.
 *     - ItemReader 및 ItemWriter 는 ItemStream 을 구현해야한다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class ItemStreamConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
//            .incrementer(new RunIdIncrementer())
            .start(step1())
            .next(step2())
            .build();
    }

    @Bean
    @JobScope
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .<String, String>chunk(5)
            .reader(itemReader())
            .writer(itemWriter())
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
        List<String> items = new ArrayList<>(10);

        for (int i = 0; i <= 10; i++) {
            items.add(String.valueOf(i));
        }

        return new CustomItemReaderV2(items);
    }

    @Bean
    public ItemWriter<String> itemWriter() {
        return new CustomItemWriterV2();
    }

}
