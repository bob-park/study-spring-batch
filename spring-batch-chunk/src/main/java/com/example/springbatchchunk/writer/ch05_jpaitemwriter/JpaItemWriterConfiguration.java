package com.example.springbatchchunk.writer.ch05_jpaitemwriter;

import com.example.springbatchchunk.reader.ch05_db.entity.CustomerEntity;
import com.example.springbatchchunk.writer.ch05_jpaitemwriter.processor.CustomItemProcessor;
import com.example.springbatchchunk.writer.model.CustomerV2;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JpaItemWriter
 * 
 * <pre>
 *     - JPA Entity 기반으로 데이터를 처리하며 EntityManagerFactory 를 주입받아 사용한다.
 *     - Entity 를 하나씩 Chunk 크기 만큼 insert 혹은 update merge 한 다음 flush 한다.
 *     - ItemReader 나 ItemProcessor 로부터 Item 을 전달받을 때 Entity Class Type 으로 받아야 한다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaItemWriterConfiguration {

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
            .<CustomerV2, CustomerEntity>chunk(5)
            .reader(itemReader())
            .processor(itemProcessor())
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
    public ItemWriter<CustomerEntity> itemWriter() {
        return new JpaItemWriterBuilder<CustomerEntity>()
//            .usePersist(true) // Entity 를 persist() 할 것인지 여부 설정, false 이면 merge() 처리
            .entityManagerFactory(entityManagerFactory)
            .build();
    }

    @Bean
    public ItemProcessor<CustomerV2, CustomerEntity> itemProcessor() {
        return new CustomItemProcessor();
    }

}
