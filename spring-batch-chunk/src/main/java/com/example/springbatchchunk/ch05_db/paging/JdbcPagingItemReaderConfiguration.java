package com.example.springbatchchunk.ch05_db.paging;

import com.example.springbatchchunk.ch05_db.model.Customer;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

/**
 * JdbcPagingItemReader
 *
 * <pre>
 *     - Paging 기반의 JDBC 구현체로써 Query 에 시작 행 번호(offset) 와 페이지에서 반환 할 행 수(limit) 를 지정해서 SQL 을 실행한다.
 *     - Spring Batch 에서 Offset 과 Limit 를 PageSize 에 맞게 자동으로 생성해주며, 페이징 단위로 데이터를 조회할 때마다 새로운 쿼리가 실행한다.
 *     - 페이지마다 새로운 쿼리를 실행하기 때문에, 페이징 시 결과 데이터의 순서가 보장될 수 있도록 order by 구문이 작성되어야 한다.
 *     - Multi-Thread 환경에서 Thread 안정성을 보장하기 때문에 별도의 동기화할 필요가 없다.
 *
 *     - PagingQueryProvider
 *          - Query 실행에 필요한 Query 를 ItemReader 에게 제공하는 Class
 *          - Database 마다 페이징 전략이 다르기때문에 각 DataBase 유형마다 다른 PagingQueryProvider 를 사용한다.
 *          - Select 절, From 절, sortKey 는 필수로 설정해야하며, where, group by 절은 필수가 아니다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class JdbcPagingItemReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Bean
    public Job batchJob1() throws Exception {
        return jobBuilderFactory.get("batchJob1")
            .incrementer(new RunIdIncrementer())
            .start(step1())
            .next(step2())
            .build();
    }

    @Bean
    public Step step1() throws Exception {
        return stepBuilderFactory.get("step1")
            .<Customer, Customer>chunk(2)
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
    public ItemReader<Customer> itemReader() throws Exception {

        Map<String, Object> parameters = new HashMap<>();

//        parameters.put("firstName", "A%");

        return new JdbcPagingItemReaderBuilder<Customer>()
            .name("jdbc-page-reader")
            .pageSize(2) // 페이지 크기 설정(Query 당 요청할 record 수)
            .dataSource(dataSource)
            .rowMapper(new BeanPropertyRowMapper<>(
                Customer.class)) // 쿼리 결과로 반환되는 데이터와 객체를 맵핑하기 위한 RowMapper 설정
            .queryProvider(createQueryProvider()) // DB 페이징 전략에 따른 PagingQueryProvider 설정
            // == PagingQueryProvider API == //
//            .selectClause("") // select 절
//            .fromClause("") // from 절
//            .whereClause("") // where 절
//            .groupClause("") // group by 절
//            .sortKeys(Map<String, Order>) // 정렬을 위한 Unique 한 키 설정
            // == PagingQueryProvider API == //
            .parameterValues(parameters) // 쿼리 파라미터 설정
//            .maxItemCount(count) // 조회할 최대 item 수
//            .currentItemCount(count) // 조회 item 의 시작 지점
//            .maxItemCount(maxRows) // ResultSet 이 포함할 수 있는 최대 행 수
            .build();
    }

    @Bean
    public PagingQueryProvider createQueryProvider() throws Exception {

        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("id, firstName, lastName, birthDate");
        queryProvider.setFromClause("from customer");
//        queryProvider.setWhereClause("where firstName like :firstName");
        queryProvider.setSortKeys(sortKeys);

        return queryProvider.getObject();
    }
}
