package com.example.springbatchchunk.ch04_jsonitemreader;

import com.example.springbatchchunk.ch04_jsonitemreader.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * JsonItemReader
 *
 * <pre>
 *     - Json Data 의 Parsing 과 Binding 을 JsonObjectReader interface 구현체에 위임하여 처리하는 ItemReader
 *     - 두가지 구현체 제공
 *          - JacksonJsonObjectReader
 *          - GsonJsonObjectReader
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class JsonItemReaderConfiguration {

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
            .<Customer, Customer>chunk(3)
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
    public ItemReader<Customer> itemReader() {
        return new JsonItemReaderBuilder<Customer>()
            .name("json-item-reader")
            .resource(new ClassPathResource("/customer.json"))
            .jsonObjectReader(new JacksonJsonObjectReader<>(Customer.class))
            .build();
    }

}
