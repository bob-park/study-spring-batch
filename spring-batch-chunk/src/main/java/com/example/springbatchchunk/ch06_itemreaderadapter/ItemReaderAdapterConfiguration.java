package com.example.springbatchchunk.ch06_itemreaderadapter;

import com.example.springbatchchunk.ch06_itemreaderadapter.service.CustomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ItemReaderAdapter
 *
 * <pre>
 *     - Batch Job 아넹서 이미 있는 Dao 나 다른 서비스를 ItemReader 안에서 사용하고자 할 때 위임 역할을 한다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class ItemReaderAdapterConfiguration {

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
            .<String, String>chunk(2)
            .reader(itemReader())
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
    public ItemReader<String> itemReader() {

        ItemReaderAdapter<String> reader = new ItemReaderAdapter<>();

        reader.setTargetObject(customService());
        reader.setTargetMethod("customRead");

        return reader;
    }

    @Bean
    public CustomService customService(){
        return new CustomService();
    }
}
