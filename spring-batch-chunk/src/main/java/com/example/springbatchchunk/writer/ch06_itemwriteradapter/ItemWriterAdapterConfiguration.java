package com.example.springbatchchunk.writer.ch06_itemwriteradapter;

import com.example.springbatchchunk.writer.ch06_itemwriteradapter.service.CustomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;

/**
 * ItemWriterAdapter
 *
 * <pre>
 *     - Batch Job 안에서 이미 있는 Dao 나 다른 서비스를 ItemWriter 안에서 사용하고자 할때 위임 역할을 한다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class ItemWriterAdapterConfiguration {

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

                    return i > 10 ? null : "item-" + i;
                }
            })
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
    public ItemWriterAdapter<String> itemWriter() {

        ItemWriterAdapter<String> adapter = new ItemWriterAdapter<>();

        adapter.setTargetObject(customService());
        adapter.setTargetMethod("customWrite");

        return adapter;
    }

    @Bean
    public CustomService<String> customService() {
        return new CustomService<>();
    }
}
