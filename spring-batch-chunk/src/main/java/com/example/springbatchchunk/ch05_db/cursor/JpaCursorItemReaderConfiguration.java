package com.example.springbatchchunk.ch05_db.cursor;

import com.example.springbatchchunk.ch05_db.entity.CustomerEntity;
import com.example.springbatchchunk.ch05_db.model.Customer;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JapCursorConfiguration
 *
 * <pre>
 *     - Spring Batch 4.3 버전부터 지원
 *     - Cursor 기반의 JPA 구현체로써 EntityManagerFactory 객체가 필요하며 Query 는 JPQL 을 사용한다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaCursorItemReaderConfiguration {

    private static final int CHUNK_SIZE = 2;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final EntityManagerFactory entityManagerFactory;

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
            .<CustomerEntity, CustomerEntity>chunk(CHUNK_SIZE)
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
    public ItemReader<CustomerEntity> itemReader() {
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("firstName", "A%");

        return new JpaCursorItemReaderBuilder<CustomerEntity>()
            .name("jpa-cursor-reader")
            .entityManagerFactory(entityManagerFactory) // JPQL 을 실행하는 EntityManager 를 생성하는 Factory
            .queryString(
                "select c from CustomerEntity c where c.firstName like :firstName") // ItemReader 가 조회할 때 사용할 JPQL 문장 설정
            .parameterValues(parameters) // Query Parameter 설정
//            .maxItemCount(count) // 조회할 최대 Item 수
//            .currentItemCount(count) // 조회 Item 시작 지점
            .build();
    }
}
