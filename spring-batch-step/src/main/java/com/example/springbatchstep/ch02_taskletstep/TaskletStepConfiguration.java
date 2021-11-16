package com.example.springbatchstep.ch02_taskletstep;

import java.util.Arrays;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TaskletStep
 *
 * <pre>
 *     - Spring Batch 에서 제공하는 Step 의 구현체, Tasklet 을 실행시키는 Domain 객체
 *     - RepeatTemplate 를 사용해 Tasklet 의 구문을 Transaction 경계 내에서 반복해서 실행함
 *     - Task 기반과 Chunk 기반으로 나누어서 Tasklet 를 실행함
 *
 *     - Chunk 기반
 *          - 하나의 큰 덩어리를 n개씩 나눠서 실행한다는 의미로 대량 처리를 하는 경우 효과적으로 설계됨
 *          - ItemReader, ItemProcessor, ItemWriter 를 사용하여 Chunk 기반 전용 Tasklet 인 ChunkOrientedTasklet 구현체 제공
 *     - Task 기반
 *          - ItemReader 와 ItemWriter 와 같은 Chunk 기반의 작업보다 단일 작업 기반으로 처리되는 것이 더 효율적인 경우
 *          - 주로 Tasklet 구현체를 만들어 사용
 *          - 대량 처리를 하는 경우 Chunk 기반에 비해 더 복잡한 구현 필요
 *
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class TaskletStepConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .incrementer(new RunIdIncrementer())
//            .start(step1())
            .start(chunkStep())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .tasklet((contribution, chunkContext) -> {
                log.info("step1 was execute");
                return RepeatStatus.FINISHED;
            }) // Tasklet class 설정, 이 메서드를 실행하면 TaskletStepBuilder 반환
//            .startLimit(10) // Step 의 실행 횟수를 설정, 설정한 만큼 실행되고 초과시 오류 발생, default : INTEGER.MAX_VALUE
//            .allowStartIfComplete(true) // Step 의 성공, 실패와 상관없이 항상 Step 을 실행하기 위한 설정
//            .listener(new StepExecutionListener() {
//                @Override
//                public void beforeStep(StepExecution stepExecution) {
//
//                }
//
//                @Override
//                public ExitStatus afterStep(StepExecution stepExecution) {
//                    return null;
//                }
//            }) // Step 의 라이프 사이클의 특정 시점에 callback 제공받도록 StepExecutionListener 설정
            .build(); // TaskletStep 생성
    }

    @Bean
    public Step chunkStep() {
        return stepBuilderFactory.get("chunkStep")
            .<String, String>chunk(10)
            .reader(
                new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5")))
            .processor((ItemProcessor<? super String, String>) String::toUpperCase)
            .writer(items -> items.forEach(log::info))
            .build();
    }
}
