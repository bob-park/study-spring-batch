package com.example.springbatchchunk.ch05_db;

import com.example.springbatchchunk.ch05_db.model.Customer;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JdbcCursorItemReader
 *
 * <pre>
 *      - Cursor 기반의 JDBC 구현체로써 ResultSet 과 함께 사용되며, Database 에서 Connection 을 얻어와서 SQL 실행한다.
 *      - Thread 안정성을 보장하지 않기 때문에, Multi-Thread 환경에서 사용할 경우 동시성 이슈가 발생하지 않도록 별도 동기화 처리가 필요하다.
 *
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class JdbcCursorItemReaderConfiguration {

    private static final int CHUNK_SIZE = 2;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

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
            .<Customer, Customer>chunk(CHUNK_SIZE)
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
        return new JdbcCursorItemReaderBuilder<Customer>()
            .name("jdbc-cursor-reader")
            // 일반적으로 fetchSize 는 chunkSize 와 동일하게 설정한다.
            .fetchSize(CHUNK_SIZE) // Cursor 방식으로 데이터를 가지고 올 때, 한번에 메모리에 할당할 크기를 설정
            .dataSource(dataSource) // DB 에 접근하기 위해 Datasource 설정
//            .rowMapper(RowMapper) // Query 결과로 반환되는 데이터와 객체를 맵핑하기 위한 RowMapper 설정
            .sql("select id, firstName, lastName, birthDate from customer where firstName like ? order by lastName, firstName") // ItemReader 가 조회할 때 사용할 쿼리 문장 설정
            .beanRowMapper(Customer.class) // 별도의 RowMapper 을 설정하지 않고, Class Type 을 설정하면, 자동으로 객체와 맵핑
            .queryArguments("A%") // Query Parameter 설정
//            .maxItemCount(count) // 조회할 최대 item 수
//            .currentItemCount(count) // 조회 item 의 시작 지점
//            .maxRows(maxRows) // ResultSet Object 가 포함할 수 있는 최대 행 수
            .build();
    }

}
