package com.example.springbatchchunk.writer.ch02_xmlstaxeventitemwriter;

import com.example.springbatchchunk.writer.model.Customer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;

/**
 * XML StaxEventItemWriter
 *
 * <pre>
 *     - XML 쓰는 과정은 읽기 과정에 대칭적이다.
 *     - StaxEventItemWriter 는 Resource, marshaller, rootTagName 이 필요하다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class XmlStaxEventItemWriterConfiguration {

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

        return new ListItemReader<>(customers);
//        return new ListItemReader<>(Collections.emptyList());
    }

    @Bean
    public ItemWriter<Customer> itemWriter() {
        return new StaxEventItemWriterBuilder<Customer>()
            .name("xml-stax-item-writer")
            .resource(new FileSystemResource(
                "/Users/hwpark/Documents/study/spring-batch/spring-batch-chunk/src/main/resources/writer/customer.xml"))
            .rootTagName("customers") // Element 의 root 가 될 이름 설정
//            .overwriteOutput(true) // 파일이 존재한다면 덮어 쓸 것인지 설정
            .marshaller(itemMarshaller()) // Marshaller 객체 설정
//            .headerCallback(callback)
//            .footerCallback(callback)
            .build();
    }

    @Bean
    public Marshaller itemMarshaller() {

        Map<String, Class<?>> aliases = new HashMap<>();

        aliases.put("customer", Customer.class);
        aliases.put("id", Long.class);
        aliases.put("name", String.class);
        aliases.put("age", Integer.class);

        XStreamMarshaller marshaller = new XStreamMarshaller();

        marshaller.setAliases(aliases);
        marshaller.setSupportedClasses(Customer.class);

        return marshaller;
    }

}
