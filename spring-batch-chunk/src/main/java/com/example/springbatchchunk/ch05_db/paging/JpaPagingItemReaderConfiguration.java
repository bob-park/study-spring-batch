package com.example.springbatchchunk.ch05_db.paging;

import com.example.springbatchchunk.ch05_db.entity.CustomerEntity;
import com.example.springbatchchunk.ch05_db.entity.Member;
import javax.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JpaPagingItemReader
 *
 * <pre>
 *     - Paging 기반의 JPA 구현체로써 EntityManagerFactory 객체가 필요하며 Query 는 JPQL 을 사용한다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaPagingItemReaderConfiguration {

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
            .<Member, Member>chunk(2)
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
    public ItemReader<Member> itemReader() {
        return new JpaPagingItemReaderBuilder<Member>()
            .name("jpa-paging-reader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(2) // 페이지 크기 설정
            .queryString("select m from Member m join fetch m.address") // ItemReader 가 조회할 때 사용할 JPQL 문장 설정
//            .parameterValues(parameters)
            .build();
    }
}
