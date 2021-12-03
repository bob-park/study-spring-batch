package com.example.springbatchtest.ch02_extra;

import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JobExplorer / JobRegistry / JobOperator
 *
 * <pre>
 *      - JobExplorer
 *          - JobRepository 의 readOnly 버전
 *          - 실행 중인 Job 의 실행 정보인 JobExecution 또는 Step 의 실행 정보인 StepExecution 을 조회할 수 있다.
 *
 *
 *      - JobRegistry
 *          - 생성된 Job 을 자동으로 등록, 추적 및 관리하며 여러 곳에서 job 을 생성한 경우, ApplicationContext 에서 job 을 수집해서 사용할 수 있다.
 *          - 기본 구현체로 Map 기반의 MapJobRegistry Class 를 제공한다.
 *              - jobName 을 key 로 하고, Job 을 값으로 하여 맵핑한다.
 *          - Job 등록
 *              - JobRegistryBeanPostProcessor - BeanPostProcessor 단계에서 bean 초기화 시 자동으로 JobRegistry 에 Job 을 등록시켜준다.
 *
 *
 *      - JobOperator
 *          - JobExplorer, JobRepository, JobRegistry, JobLauncher 를 포함하고 있으며, Batch 의 중단, 재시작, Job 요약 등의 모니터링이 가능하다.
 *          - 기본 구현체로 SimpleJobOperator class 를 제공한다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class ExtraConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final JobRegistry jobRegistry; // Spring Batch 에서 초기화시 bean 에 주입된다.

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
            .tasklet((contribution, chunkContext) -> {
                log.info("step1 was executed.");

                sleep(3_000);

                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet((contribution, chunkContext) -> {
                log.info("step2 was executed.");

                sleep(3_000);

                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public BeanPostProcessor jobRegistryBeanPostProcessor() {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();

        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);

        return jobRegistryBeanPostProcessor;
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.warn(e.getMessage());
        }
    }


}
