package com.example.springbatchflow.ch03_custom_exitstatus;

import com.example.springbatchflow.ch03_custom_exitstatus.listener.PassCheckingListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Custom ExitStatus
 *
 * <pre>
 *     - ExitStatus 에 존재하지 않는 status 를 새롭게 정의
 *     - StepExecutionListener 의 afterStep() 메서드와 Custom ExitCode 생성 후 새로운 ExitStatus 반환
 *     - Step 실행 후 완료 시점에서 현재 ExitCode 를 사용자 정의 ExitCode 로 수정할 수 있음
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class CustomExitStatusConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        // step1 (FAILED) => step2 (COMPLETED) : Job FAILED? 왜?
        // ! transition 으로 step2 의 ExitStatus 의 "PASS" 가 아닌것에 대한 다음으로 지정된게 없으므로, step2 는 COMPLETED 지만, 내부적으로 step2 는 FAILED 로 처리
        // step1 (FAILED) => step2 (PASS) : Job STOPPED
        return jobBuilderFactory.get("batchJob1")
            .incrementer(new RunIdIncrementer())
            .start(step1())
            .on("FAILED")
            .to(step2())
            .on("PASS")
            .stop()
            .end()
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .tasklet((contribution, chunkContext) -> {
                log.info(" >> step1 was executed.");
                contribution.setExitStatus(ExitStatus.FAILED);
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet((contribution, chunkContext) -> {
                log.info(" >> step2 was executed.");
                return RepeatStatus.FINISHED;
            })
            .listener(new PassCheckingListener())
            .build();
    }
}
