package com.example.springbatchjob.ch03_simplejob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SimpleJob
 *
 * <pre>
 *     - SimpleJob 은 Step 을 실행시키는 Job 구현체, SimpleJobBuilder 에 의해 생성됨
 *     - 여러 단계의 Step 으로 구성할 수 있으며, Step 을 순차적으로 실행시킨다.
 *     - 모든 Step 의 실행이 성공적으로 완료되어야 Job 이 성공적으로 완료된다.
 *     - 맨 마지막에 실행한 Step 의 BatchStatus 가 Job 의 최종 BatchStatus 가 된다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1") // JobBuilder 를 생성하는 Factory, Job 의 이름을 매개변수로 받음
            .start(step1()) // 처음 실행할 Step 설정, 최초 한번 설정, 이 메서드를 실행하면 SimpleJobBuilder 반환
            .next(step2()) // 다음 실행할 Step 설정, 횟수 제한 없음, 모든 next() 의 Step 이 종료가 되면 Job 이 종료
            .next(step3())
            .incrementer(
                new RunIdIncrementer()) // JobParameter 의 값을 자동으로 증가해주는 JobParameterIncrementer 설정
//            .preventRestart() // Job 의 재시작을 불가능으로 설정
            .validator(parameters -> {

            }) // JobParameter 를 실행하기 전에 올바른 구성이 되어 있는지 검증하는 JobParameterValidator 설정
            .listener(new JobExecutionListener() {
                @Override
                public void beforeJob(JobExecution jobExecution) {
                    log.info("before job...");
                }

                @Override
                public void afterJob(JobExecution jobExecution) {
                    log.info("after job...");
                }
            }) // job life cycle 의 특정 시점에 callback 제공 받도록 JobExecutionListener 설정
            .build(); // SimpleJob 생성
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

    @Bean
    public Step step3() {
        return stepBuilderFactory.get("step3")
            .tasklet((contribution, chunkContext) -> {

                // BatchStatus : Failed
                // ExitStatus : Stopped
                // ! Job 의 BatchStatus 와 ExitStatus 는 최종 Step 의 Status 를 따른다.
                chunkContext.getStepContext().getStepExecution().setStatus(BatchStatus.FAILED); // 이 Step 의 Status 를 인의적으로 Failed 로 설정

                contribution.setExitStatus(ExitStatus.STOPPED);

                log.info("step3 was execute");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

}
