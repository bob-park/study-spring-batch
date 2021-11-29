package com.example.springbatchmultithread.ch02_async;

import com.example.springbatchmultithread.ch02_async.listener.StopWatchJobListener;
import com.example.springbatchmultithread.ch02_async.mapper.CustomerRowMapper;
import com.example.springbatchmultithread.ch02_async.model.Customer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

/**
 * AsyncITemProcessor / AsyncItemWriter
 *
 * <pre>
 *      - 기본 개념
 *          - Step 안에서 ItemProcessor 가 비동기적으로 동작하는 구조
 *          - AsyncItemProcessor 와 AsyncItemWriter 가 함께 구성이 되어야함
 *          - AsyncItemProcessor 로부터 AsyncItemWriter 가 받는 최종 결과값은 List<Future<T>> 타입이며 비동기 실행이 완료될때 까지 대기한다.
 *          - spring-batch-integration 의존성이 필요하다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class AsyncConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .incrementer(new RunIdIncrementer())
//            .start(step1())
            .start(asyncStep1())
            .listener(new StopWatchJobListener())
            .build();
    }

    /**
     * 동기식 Step
     *
     * @return
     */
    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .<Customer, Customer>chunk(100)
            .reader(pagingItemReader())
            .processor(customItemProcessor())
            .writer(customItemWriter())
            .build();
    }

    /**
     * 비동기식 Step
     *
     * @return
     */
    @Bean
    public Step asyncStep1() {
        return stepBuilderFactory.get("asyncStep1")
            .<Customer, Future<Customer>>chunk(100)
            .reader(pagingItemReader()) // 일반적인 ItemReader
            /*
             * 비동기 실행을 위한 AsyncItemProcessor 설정
             *  - Thread Poll 개수 만큼 Thread 가 생성되어 비동기로 실행
             *  - 내부적으로 실제 ItemProcessor 에게 실행을 위임하고 결과를 Future 에 저장
             */
            .processor(asyncItemProcessor())
            /*
             * AsyncItemWriter 설정
             *  - 비동기 실행 결과값들을 모두 받아오기까지 대기함
             *  - 내부적으로 실제 ItemWriter 에게 최종 결과값을 넘겨주고 실행을 위임한다.
             */
            .writer(asyncItemWriter())
            .build();
    }

    @Bean
    public ItemWriter<Future<Customer>> asyncItemWriter() {

        AsyncItemWriter<Customer> asyncItemWriter = new AsyncItemWriter<>();

        asyncItemWriter.setDelegate(customItemWriter()); // 위임할 itemWriter 를 넣은다.

        return asyncItemWriter;
    }

    @Bean
    public ItemProcessor<Customer, Future<Customer>> asyncItemProcessor() {

        AsyncItemProcessor<Customer, Customer> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(customItemProcessor()); // 위임할 ItemProcessor 를 넣어준다.
        // Spring 에서 제공해주는 AsyncTaskExecutor 사용
        asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());

        return asyncItemProcessor;
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
    public ItemProcessor<Customer, Customer> customItemProcessor() {

        return item -> {
            sleep(100);

            return new Customer(item.getId(),
                item.getFirstName().toLowerCase(),
                item.getLastName().toLowerCase(),
                item.getBirthDate());
        };

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
