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
import org.springframework.batch.item.file.transform.Range;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * Exception Handling
 *
 * <pre>
 *      - 라인을 읽거나 토큰화할때 발생하는 Parsing 예외를 처리할 수 있도록 예외 계층 제공
 *      - 토큰화 검증을 엄격하게 적용하지 않도록 설정하면 Parsing 예외가 발생하지 않도록 할 수 있다.
 *
 *      - 토큰화 검증 기준 설정
 *          - LineTokenizer 의 Strict 속성을 false 로 설정하게 되면, Tokenizer 가 라인 길이를 검증하지 않는다.
 *          - Tokenizer 가 라인 길이나 컬럼명을 검증하지 않는 경우 예외가 발생하지 않는다.
 *          - FieldSet 은 성공적으로 리턴이 되며, 두번째 범위값은 빈 토큰을 가지게 된다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class ExceptionHandlingConfiguration {

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
            .name("flatFile")
            .resource(new ClassPathResource("/customer.txt"))
            .encoding(StandardCharsets.UTF_8.name())
            .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
            .targetType(Customer.class)
            .linesToSkip(1)
            .fixedLength()
            .strict(false) // false 설정 시 길이에 대한 토큰화 검증을 진행하지 않는다.
            .addColumns(new Range(1, 5))
            .addColumns(new Range(6, 7))
            .addColumns(new Range(8, 11))
            .names("name", "age", "year")
            .build();
    }


}
