package com.example.springbatch.configure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JobRepository
 *
 * <pre>
 *     - Batch 작업중의 정보를 저장하는 저장소 역할
 *     - Job 이 언제 수행, 언제 끝났으며, 몇번 실행되었는지 등 CRUD 를 담당
 *
 *     - @EnableBatchProcessing 을 선언하면 JobRepository 가 자동으로 빈을 생성
 *     - BatchConfigurer 인터페이스를 구현하거나 BasicBatchConfigurer 를 상속하여 JobRepository 설정을 커스터마이징 할 수 있다.
 *
 *     - JDBC 방식 (JobRepositoryFactoryBean)
 *          - 내부적으로 AOP 기술을 통해 트랜잭션 처리를 해주고 있음
 *          - 트랜잭션 Isolation 의 기본값은 Serializable 으로 최고 수준, 다른 레벨(READ_COMMITTED, REPEATABLE_READ) 로 지정 가능
 *          - 메타테이블의 table prefix 를 변경 가능, default: BATCH_
 *
 *     - In Memory 방식 (MapJobRepositoryFactoryBean)
 *          - 성능 등의 이유로 Domain Object 를 굳이 데이터베이스에 저장하고 싶지 않을 경우
 *          - 보통 Test 나 프로토타입의 빠른 개발이 필요할때 사용
 *
 * </pre>
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobRepositoryConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final JobExecutionListener jobExecutionListener;

    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
            .start(step1())
            .next(step2())
            .listener(jobExecutionListener)
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .tasklet((contribution, chunkContext) -> {
                log.info("step1 was execute");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet((contribution, chunkContext) -> {
                log.info("step2 was execute");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

}
