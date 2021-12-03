package com.example.springbatchtest.ch01_spring_batch_test;

import com.example.springbatchtest.common.mapper.CustomerRowMapper;
import com.example.springbatchtest.common.model.Customer;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .start(step1())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .<Customer, Customer>chunk(2)
            .reader(pagingItemReader())
            .writer(customItemWriter())
            .build();
    }

    @Bean
    public JdbcPagingItemReader<Customer> pagingItemReader() {
        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, first_name, last_name, birth_date");
        queryProvider.setFromClause("from customer");
        queryProvider.setSortKeys(sortKeys);

        return new JdbcPagingItemReaderBuilder<Customer>()
            .name("jdbc-paging-reader")
            .dataSource(dataSource)
            .fetchSize(300)
            .rowMapper(new CustomerRowMapper())
            .queryProvider(queryProvider)
            .build();
    }

    @Bean
    public ItemWriter<Customer> customItemWriter() {
        return new JdbcBatchItemWriterBuilder<Customer>()
            .dataSource(dataSource)
            .sql("insert into customer2 values (:id, :firstName, :lastName, :birthDate)")
            .beanMapped()
            .build();
    }

    private void sleep(int millis) {
        try {
            log.info("sleep...");
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }

    }


}
