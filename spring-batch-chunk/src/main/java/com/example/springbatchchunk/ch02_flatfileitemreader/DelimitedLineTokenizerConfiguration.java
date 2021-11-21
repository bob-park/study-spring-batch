package com.example.springbatchchunk.ch02_flatfileitemreader;

import com.example.springbatchchunk.ch02_flatfileitemreader.model.Customer;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

/**
 * DelimitedLineTokenizer
 *
 * <pre>
 *      - DelimitedLineTokenizer
 *          - 한 개 라인의 String 을 구분자 기준으로 나누어 토큰화 하는 방식
 *
 *
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class DelimitedLineTokenizerConfiguration {

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
            .<Customer, Customer>chunk(5)
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

        return new FlatFileItemReaderBuilder<Customer>()
            .name("customer")
            .resource(new ClassPathResource("/customer.csv"))
            .encoding(StandardCharsets.UTF_8.name())
            .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
            .targetType(Customer.class)
            .linesToSkip(1)
            .delimited().delimiter(",")
            .names("name", "age", "year")
            .build();
    }


}
