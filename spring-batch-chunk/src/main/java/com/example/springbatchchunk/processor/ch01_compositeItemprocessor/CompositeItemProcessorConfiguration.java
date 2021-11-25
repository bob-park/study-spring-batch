package com.example.springbatchchunk.processor.ch01_compositeItemprocessor;

import com.example.springbatchchunk.processor.ch01_compositeItemprocessor.custom.CustomItemProcessor1;
import com.example.springbatchchunk.processor.ch01_compositeItemprocessor.custom.CustomItemProcessor2;
import java.util.ArrayList;
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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;

/**
 * CompositeItemProcessor
 *
 * <pre>
 *     - ItemProcessor 들을 연결 (chaining) 해서 위임하면 각 ItemProcessor 를 실행시킨다.
 *     - 이전 ItemProcessor 반환값은 다음 ItemProcessor 값으로 연결된다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class CompositeItemProcessorConfiguration {

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
            .reader(itemReader())
            .processor(itemProcessor())
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
        List<String> items = Arrays.asList("item1", "item2", "item3", "item4", "item5");

        return new ListItemReader<>(items);
    }

    @Bean
    public ItemProcessor<String, String> itemProcessor() {

        List<ItemProcessor<String, String>> processors = new ArrayList<>();

        processors.add(new CustomItemProcessor1());
        processors.add(new CustomItemProcessor2());

        return new CompositeItemProcessorBuilder<String, String>()
            .delegates(processors) // Chaining 할 ItemProcessor 객체 설정
            .build();
    }

    @Bean
    public ItemWriter<String> itemWriter() {
        return items -> log.info("items={}", items);
    }
}
