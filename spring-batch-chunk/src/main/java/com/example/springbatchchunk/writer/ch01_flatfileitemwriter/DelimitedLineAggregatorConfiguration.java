package com.example.springbatchchunk.writer.ch01_flatfileitemwriter;

import com.example.springbatchchunk.writer.model.Customer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

/**
 * DelimitedLineAggregator & FormatterLineAggregator
 *
 * <pre>
 *     - DelimitedLineAggregator
 *          - 객체의 필드 사이에 구분자를 사입해서 한 문자열로 변환한다.
 *
 *      - FormatterLineAggregator
 *          - 객체의 필드를 사용자가 설정한 Formatter 구문을 통해 문자열로 변환한다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class DelimitedLineAggregatorConfiguration {

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
    public ItemReader<Customer> itemReader() {

        List<Customer> customers = Arrays.asList(
            new Customer(1L, "hong gill dong1", 1),
            new Customer(2L, "hong gill dong2", 2),
            new Customer(3L, "hong gill dong3", 3)
        );

        return new ListItemReader<>(Collections.emptyList());
    }

    @Bean
    public ItemWriter<Customer> itemWriter() {
        return new FlatFileItemWriterBuilder<Customer>()
            .name("flat-file-writer")
            .resource(new FileSystemResource("/Users/hwpark/Documents/study/spring-batch/spring-batch-chunk/src/main/resources/writer/customer.txt"))
            .append(true)
            .shouldDeleteIfEmpty(true)
            .delimited().delimiter("|")
            .names("id", "name", "age")
            .build();
    }

}
