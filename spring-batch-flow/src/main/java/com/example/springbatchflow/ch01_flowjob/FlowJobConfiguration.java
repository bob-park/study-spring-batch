package com.example.springbatchflow.ch01_flowjob;

import static org.springframework.batch.core.ExitStatus.COMPLETED;
import static org.springframework.batch.core.ExitStatus.FAILED;

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
 * FlowJob
 *
 * <pre>
 *     - Step 을 순차적으로만 구성하는 것이 아닌, 특정한 상태에 따라 흐름을 전환하도록 구성할 수 있다.
 *     - FlowJobBuilder 에 의해 생성
 *          - Step 이 실패하더라도 Job 은 실패로 끝나지 않도록 해야하는 경우
 *          - Step 이 성공했을때, 다음 실행해야할 Step 을 구분해서 실행해야 하는 경우
 *          - 특정 Step 은 전혀 실행되지 않게 구성해야 하는 경우
 *     - Flow 와 Job 의 흐름을 구성하는데만 관여하고 실제 비지니스 로직은 Step 에서 이루어 진다.
 *     - 내부적으로 SimpleFlow 객체를 포함하고 있으며, Job 실행 시 호출한다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class FlowJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {

        // step1 -> completed -> step3
        // step1 -> failed -> step2
        return jobBuilderFactory.get("batchJob1")
            .incrementer(new RunIdIncrementer())
            // <-- -->
            // Flow 의 흐름을 정의하는 역할
            .start(step1()) // Flow 시작하는 Step 설정
            // </-- -->
            // <--  -->
            // - 조건에 따라서 흐름을 전환시키는 역할
            // - Step 간 조건부 전환을 구성할수 있게 된다.
            .on(COMPLETED.getExitCode()) // Step 의 실행 결과로 돌려받는 종료상태(ExitStatus) 를 catch 하여 매칭하는 패턴, TransitionBuilder 반환
            // </-- -->
            // <-- -->
            // - 조건에 따라서 흐름을 전환시키는 역할
            // Flow 를 중지 / 실패 / 종료 하도록 Flow 종료
            .to(step3()) // 다음으로 이동할 Step 지정
//            .stop() // 중지
//            .fail() // 실패
//            .end() // 종료
//            .stopAndRestart() // 중지 and 재시작
            // <-- -->
            // <-- -->
            // - 조건에 따라서 흐름을 전환시키는 역할
            .from(step1()) // 이전 단계에서 정의한 Step 의 Flow 를 추가적으로 정의함
            // </-- -->
            .on(FAILED.getExitCode())
            .to(step2())
            // <-- -->
            // Flow 의 흐름을 정의하는 역할
//            .next(step4()) // 다음으로 이동할 Step 지정
            // </-- -->
            .end() // build() 앞에 위치하면, FlowBuilder 를 종료하고, SimpleFlow 객체 생성
            .build(); // FlowJob 생성하고, Flow 필드에 SimpleFlow 저장

    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .tasklet((contribution, chunkContext) -> {
                log.info("step1 was execute");
                throw new RuntimeException("step2 was failed.");
//                return RepeatStatus.FINISHED;
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
                log.info("step3 was execute");
                return RepeatStatus.FINISHED;
            })
            .build();
    }
}
