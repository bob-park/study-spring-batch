package com.example.springbatchchunk.writer.ch04_jdbcbatchitemwriter;

import com.example.springbatchchunk.writer.model.CustomerV2;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JdbcBatchItemWriter
 *
 * <pre>
 *     - JdbcCursorItemReader 설정과 마찬가지로 datasource 를 지정하고, sql 속성이 실행할 쿼리를 설정
 *     - JDBC 의 Batch 기능을 사용하여, bulk insert/update/delete 방식으로 처리
 *     - 단건 처리가 아닌 일괄처리이기 때문에 성능에 이점을 가진다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class JdbcBatchItemWriterConfiguration {

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
            .<CustomerV2, CustomerV2>chunk(5)
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
    public ItemReader<CustomerV2> itemReader() {

        List<CustomerV2> customers = Arrays.asList(
            new CustomerV2(1L, "gil dong 1", "hong", "2021-12-01 00:00:00AM"),
            new CustomerV2(2L, "gil dong 2", "hong", "2021-12-01 00:00:00AM"),
            new CustomerV2(3L, "gil dong 3", "hong", "2021-12-01 00:00:00AM")
        );

        return new ListItemReader<>(customers);
//        return new ListItemReader<>(Collections.emptyList());
    }

    @Bean
    public ItemWriter<CustomerV2> itemWriter() {
        return new JdbcBatchItemWriterBuilder<CustomerV2>()
            .dataSource(dataSource)
            .sql("insert into customer values(:id, :firstName, :lastName, :birthDate)") // ItemWriter 가 사용할 쿼리 문장 설정
//            .assertUpdates(true) // Transaction 이 후 적어도 하나의 항목이 행을 업데이터 또는 삭제하지 않을 경우 예외발생 여부를 설정함, default : true
            .beanMapped() // POJO 기반으로 insert SQL 의 values 를 맵핑
//            .columnMapped() // key, value 기반으로 insert SQL 의 values 를 맵핑
            .build();
    }
}
