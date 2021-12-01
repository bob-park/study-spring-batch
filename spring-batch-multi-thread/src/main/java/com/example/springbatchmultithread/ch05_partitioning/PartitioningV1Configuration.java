package com.example.springbatchmultithread.ch05_partitioning;

import com.example.springbatchmultithread.ch05_partitioning.partitioner.ColumnRangePartitioner;
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
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Partitioning - 1
 *
 * <pre>
 *      - MasterStep 이 SlaveStep 을 실행시키는 구조
 *      - SlaveStep 은 각 Thread 에 의해 독립적으로 실행됨
 *      - SlaveStep 은 독집적인 StepExecution 파라미터 환경을 구성함
 *      - SlaveStep 은 ItemReader / ItemProcessor / ItemWriter 등을 가지고 동작하며, 작업을 독립적으로 병렬 처리한다.
 *      - MasterStep 은 PartitionStep 이며 SlaveStep 은 TaskletStep, FlowStep 등이 올 수 있다.
 *
 *
 *      - PartitionStep
 *          - Partitioning 기능을 수행하는 Step 구현체
 *          - Partitioning 을 수행 후 StepExecutionAggregator 를 사용해서 StepExecution 의 정보를 최종 집계한다.
 *
 *      - PartitionHandler
 *          - PartitionStep 에 의해 호출되며 Thread 를 생성해서 WorkStep 을 병렬로 실행한다.
 *          - WorkStep 에서 사용할 StepExecution 생성은 StepExecutionSplitter 와 Partitioner 에게 위임한다.
 *          - WorkStep 을 병렬로 실행 후 최종 결과를 담은 StepExecution 을 PartitionStep 에 반환한다.
 *
 *      - StepExecutionSplitter
 *          - WorkStep 에서 사용할 StepExecution 을 gridSize 만큼 생성한다.
 *          - Partitioner 를 통해 ExecutionContext 를 얻어서 StepExecution 에 맵핑한다.
 *
 *      - Partitioner
 *          - StepExecution 에 맵핑 할 ExecutionContext 를 gridSize 만큼 생성한다.
 *          - 각 ExecutionContext 에 저장된 정보는 WorkStep 을 실행하는 Thread 마다 독립적으로 참조 및 활용이 가능하다.
 *
 *
 *      - 각 Thread 는 자신에게 할당된 StepExecution 을 가지고 있다.
 *      - 각 Thread 는 자신에게 할당된 Chunk Class 를 참조한다.
 *      - Thread-safe 를 만족한다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class PartitioningV1Configuration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .incrementer(new RunIdIncrementer())
            .start(masterStep())
            .build();
    }

    @Bean
    public Step masterStep() {
        return stepBuilderFactory.get("masterStep")
            // PartitionStep 생성을 위한 PartitionStepBuilder 가 생성되고 Partitioner 를 설정
            .partitioner(slaveStep().getName(), partitioner())
            .step(slaveStep()) // Slave 역할을 하는 Step 을 설정 : TaskletStep, FlowStep 등이 올 수 있음
            .gridSize(4) // Partition 구분을 위한 값 설정 : 몇개의 Partition 으로 나눌 것인지 사용됨
            .taskExecutor(taskExecutor())
            .build();
    }

    @Bean
    public Step slaveStep() {
        return stepBuilderFactory.get("slaveStep")
            .<Customer, Customer>chunk(100)
            /*
             ! @StepScope 를 선언할 경우 Proxy 객체가 생성되기 때문에, Compile Error 를 방지하기 위해 parameter 에 null 을 넣어준다.
             ! 해당 parameter 는 실제 Bean 생성된 후 선언한 값이 들어간다.
             */
            .reader(pagingItemReader(null, null))
            .writer(customItemWriter())
            .build();
    }

    @Bean
    public Partitioner partitioner() {

        ColumnRangePartitioner columnRangePartitioner = new ColumnRangePartitioner();

        columnRangePartitioner.setColumn("id"); // id 를 구분자로 하여 partitioning
        columnRangePartitioner.setDataSource(dataSource);
        columnRangePartitioner.setTable("customer"); // source table

        return columnRangePartitioner;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(2);
        taskExecutor.setMaxPoolSize(4);
        taskExecutor.setThreadNamePrefix("async-thread-");

        return taskExecutor;
    }

    @Bean
    @StepScope // ItemReader 부분이기 때문에 @StepScope 를 선언해야한다.
    public JdbcPagingItemReader<Customer> pagingItemReader(
        @Value("#{stepExecutionContext['minValue']}") Long minValue,
        @Value("#{stepExecutionContext['maxValue']}") Long maxValue
    ) {

        log.info("reading {} to {}", minValue, maxValue);

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("id, first_name, last_name, birth_date");
        queryProvider.setFromClause("from customer");
        /*
         * Partitioner 인해 Thread 마다 각자의 StepExecutionContext 를 가지게 될 것이고,
         * 각 StepExecutionContext 에 minValue 와 maxValue 를 가지고 될 것이다.
         */
        queryProvider.setWhereClause("where id >= " + minValue + " and id <= "
            + maxValue); // minValue 와 maxValue 는 StepExecutionContext 에서 가져와야한다.
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
