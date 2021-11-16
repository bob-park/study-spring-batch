package com.example.springbatchstep.ch02_taskletstep;

import com.example.springbatchstep.ch02_taskletstep.tasklet.CustomTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tasklet - tasklet()
 *
 * <pre>
 *     - Tasklet Type 의 class 를 설정
 *          - Tasklet
 *              - Step 내에서 구성되고 실행되는 domain 객체, 주로 단일 task 로 수행
 *              - TaskletStep 에 의해 반복적으로 수행되며, 반환값에 따라 계속 수행 및 종료한다.
 *              - RepeatStatus - Tasklet 의 반복 여부 상태 값
 *                  - FINISHED : Tasklet 종료, RepeatStatus 를 null 로 반환하면 FINISHED 로 해석됨
 *                  - CONTINUABLE : Tasklet 반복
 *                  - FINISHED 가 리턴되거나 실패 예외가 던져지기 전까지 TaskletStep 에 의해 While 문 안에서 반복적으로 호출됨(무한 루프 주의)
 *
 *
 *      - 익명 클래스 혹은 구현 클래스를 만들어서 사용
 *      - 이 메소드를 실행하게 되면, TaskletStepBuilder 가 반환되어, 관련 API 를 설정할 수 있다.
 *      - Step 에 오직 하나의 Tasklet 설정이 가능하며, 두개 이상을 설정했을 경우 마지막에 설정한 객체가 실행된다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class TaskletConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

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
                log.info("step1 was execute");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet(new CustomTasklet())
            .build();
    }
}
