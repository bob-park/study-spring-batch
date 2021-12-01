package com.example.springbatchmultithread.ch06_synchronizedItemStreamReader;

import com.example.springbatchmultithread.mapper.CustomerRowMapper;
import com.example.springbatchmultithread.model.Customer;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * SynchronizedItemStreamReader
 *
 * <pre>
 *      - Thread-safe 하지 않는 ItemReader 를 Thread-safe 하게 처리하도록 하는 역할을 한다.
 *          - 일반적으로 Spring Batch 가 제공하는 대부분의 ItemReader 는 Thread-safe 하지 않는다.
 *      - Spring Batch 4.0 부터 지원
 *
 *      - SynchronizedItemStreamReader 는 Thread-safe 를 위한 동기화 처리를 해주고 데이터 처리는 ItemReader 에게 위임한다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class SynchronizedItemStreamReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .incrementer(new RunIdIncrementer())
            .start(step1())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .<Customer, Customer>chunk(2)
            .reader(customItemReader())
            .listener(new ItemReadListener<>() {
                @Override
                public void beforeRead() {

                }

                @Override
                public void afterRead(Customer item) {
                    log.info("thread={}, item.id={}",
                        Thread.currentThread().getName(),
                        item.getId());
                }

                @Override
                public void onReadError(Exception ex) {

                }
            })
            .writer(customItemWriter())
            .taskExecutor(taskExecutor())
            .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(8);
//        taskExecutor.setThreadNamePrefix("not-thread-safe-");
        taskExecutor.setThreadNamePrefix("thread-safe-");

        return taskExecutor;
    }

    @Bean
    @StepScope
    public SynchronizedItemStreamReader<Customer> customItemReader() {
        /*
         ! Not Thread-safe ItemReader

         CursorItemReader 사용시 Thread 간 값의 공유가 발생하면 Exception 이 발생함
         */
//        return new JdbcCursorItemReaderBuilder<Customer>()
//            .name("jdbc-cursor-reader")
//            .dataSource(dataSource)
//            .fetchSize(2)
//            .rowMapper(new CustomerRowMapper())
//            .sql("select id, first_name, last_name, birth_date from customer")
//            .build();

        /*
         * Thread-safe ItemReader
         */
        JdbcCursorItemReader<Customer> cursorItemReader = new JdbcCursorItemReaderBuilder<Customer>()
            .name("jdbc-cursor-reader")
            .dataSource(dataSource)
            .fetchSize(2)
            .rowMapper(new CustomerRowMapper())
            .sql("select id, first_name, last_name, birth_date from customer")
            .build();

        return new SynchronizedItemStreamReaderBuilder<Customer>()
            .delegate(cursorItemReader)
            .build();
    }

    @Bean
    @StepScope // ItemWriter 도 병렬로 수행할려면 @StepScope 를 선언해주어야 한다.
    public ItemWriter<Customer> customItemWriter() {
        return new JdbcBatchItemWriterBuilder<Customer>()
            .dataSource(dataSource)
            .sql("insert into customer2 values (:id, :firstName, :lastName, :birthDate)")
            .beanMapped()
            .build();
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }

    }

}
