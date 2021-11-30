package com.example.springbatchmultithread.ch03_multi_threaded_step;

import com.example.springbatchmultithread.listener.CustomItemProcessListener;
import com.example.springbatchmultithread.listener.CustomItemReadListener;
import com.example.springbatchmultithread.listener.CustomItemWriteListener;
import com.example.springbatchmultithread.listener.StopWatchJobListener;
import com.example.springbatchmultithread.mapper.CustomerRowMapper;
import com.example.springbatchmultithread.model.Customer;
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
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Multi-threaded Step
 *
 * <pre>
 *      - Step 내에서 Multi-Thread 로 chunk 기반 처리가 이루어지는 구조
 *      - TaskExecutorRepeatTemplate 이 반복자로 사용되며 설정한 개수 (ThrottleLimit : default 4개) 만큼의 Thread 를 생성하여 수행한다.
 *
 *      - ItemReader 는 Thread-safe 인지 반드시 확인해야 한다.
 *          - 데이터를 소스로부터 읽어오는 역할이기 때문에 Thread 마다 중복해서 데이터를 읽어오지 않도록 동기화가 보장되어 있어야 한다.
 *      - Thread 마다 새로운 Chunk 가 할당되어 데이터 동기화가 보장된다.
 *          - Thread 끼리 Chunk 를 서로 공유하지 않는다.
 *
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class MultiThreadedStepConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .incrementer(new RunIdIncrementer())
            .start(step1())
            .listener(new StopWatchJobListener())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .<Customer, Customer>chunk(2)
            .reader(pagingItemReader())
            .listener(new CustomItemReadListener()) // ItemReadListener
            .processor(customItemProcessor())
            .listener(new CustomItemProcessListener()) // ItemProcessListener
            .writer(customItemWriter())
            .listener(new CustomItemWriteListener()) // ItemWriteListener
            // SimpleAsyncTaskExecutor 는 Spring 에서 제공하는 비동기 ThreadExecutor
//            .taskExecutor(new SimpleAsyncTaskExecutor()) // Thread 생성 및 실행을 위한 taskExecutor 설정
            .taskExecutor(taskExecutor()) // 설정하지 않을 경우 Single Thread 로 실행됨
            .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        // Java 에서 제공하는 ThreadPoolTaskExecutor 를 사용해서 Thread Pool 을 생성하는 것을 권장
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(4); // 동시에 실행될 thread 개수
        taskExecutor.setMaxPoolSize(8); // 최대 thread 생성 개수
        taskExecutor.setThreadNamePrefix("async-thread-"); // thread 이름의 prefix

        return taskExecutor;
    }

    @Bean
    public JdbcPagingItemReader<Customer> pagingItemReader() {
        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, first_name, last_name, birth_date");
        queryProvider.setFromClause("from customer");
        queryProvider.setSortKeys(sortKeys);

        // ! JdbcCursorItemReader / JpaCursorItemReader 인 경우 Thread-safe 를 제공해 주지않음
        // * JdbcPagingItemReader / JpaPagingItemReader 가 Thread-safe 를 제공함
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
