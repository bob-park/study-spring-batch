package com.example.springbatchmultithread.ch04_parallel_steps;

import com.example.springbatchmultithread.ch04_parallel_steps.tasklet.CustomTasklet;
import com.example.springbatchmultithread.listener.StopWatchJobListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Parallel Steps
 *
 * <pre>
 *      - SplitState 를 사용해서 여러개의 Flow 들을 병렬적으로 실행하는 구조
 *      - 실행이 다 완료된 후 FlowExecutionStatus 결과들을 취합해서 다음 단계 결정을 한다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class ParallelStepsConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .incrementer(new RunIdIncrementer())
            .start(flow1())
            /*
             flow2 와 flow3 을 생성하고 총 3개의 flow 를 합친다.
             - taskExecutor 에서 flow 개수만큼 thread 를 생성해서 각 flow 를 실행시킨다
             */
//            .split(taskExecutor()).add(flow2(), flow3())
            .split(taskExecutor()).add(flow2())
//            .next(flow4()) // split 처리가 완료된 후 실행이 된다.
            .end()
            .listener(new StopWatchJobListener())
            .build();
    }

    @Bean
    public Flow flow1() {

        TaskletStep step = stepBuilderFactory.get("step1")
            .tasklet(tasklet())
            .build();

        return new FlowBuilder<Flow>("flow1")
            .start(step)
            .build();
    }

    @Bean
    public Flow flow2() {

        TaskletStep step2 = stepBuilderFactory.get("step2")
            .tasklet(tasklet())
            .build();

        TaskletStep step3 = stepBuilderFactory.get("step3")
            .tasklet(tasklet())
            .build();

        return new FlowBuilder<Flow>("flow2")
            .start(step2)
            .next(step3)
            .build();
    }

    @Bean
    public Flow flow3() {
        TaskletStep step4 = stepBuilderFactory.get("step4")
            .tasklet(tasklet())
            .build();

        return new FlowBuilder<Flow>("flow3")
            .start(step4)
            .build();
    }

    @Bean
    public Flow flow4() {
        TaskletStep step5 = stepBuilderFactory.get("step5")
            .tasklet(tasklet())
            .build();

        return new FlowBuilder<Flow>("flow4")
            .start(step5)
            .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(2);
        taskExecutor.setMaxPoolSize(4);
        taskExecutor.setThreadNamePrefix("async-thread-");

        return taskExecutor;
    }

    @Bean
    public Tasklet tasklet() {
        return new CustomTasklet();
    }

    private void sleep(int millis) {
        try {
            log.info("sleep...");
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }

    }
}
