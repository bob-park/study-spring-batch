package com.example.springbatchflow.ch04_jobexecutiondecider;

import com.example.springbatchflow.ch04_jobexecutiondecider.decider.CustomJobExecutionDecider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JobExecutionDecider
 *
 * <pre>
 *     - ExitStatus 를 조작하거나 StepExecutionListener 를 등록할 필요 없이 Transition 처리를 위한 전용 Class
 *     - Step 과 Transition 역할을 명확히 분리해서 설정할 수 있음
 *     - Step 의 ExitStatus 가 아닌 JobExecutionDecider 의 FlowExecutionStatus 값을 새롭게 설정하여 반환함
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class JobExecutionDeciderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .incrementer(new RunIdIncrementer())
            .start(startStep())
            .next(decider())
            .from(decider()).on("ODD").to(oddStep())
            .from(decider()).on("EVEN").to(evenStep())
            .end()
            .build();
    }

    @Bean
    public JobExecutionDecider decider() {
        return new CustomJobExecutionDecider();
    }

    @Bean
    public Step startStep() {
        return stepBuilderFactory.get("startStep")
            .tasklet((contribution, chunkContext) -> {
                log.info(" >> startStep was executed.");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step evenStep() {
        return stepBuilderFactory.get("evenStep")
            .tasklet((contribution, chunkContext) -> {
                log.info(" >> evenStep was executed.");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step oddStep() {
        return stepBuilderFactory.get("oddStep")
            .tasklet((contribution, chunkContext) -> {
                log.info(" >> oddStep was executed.");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

}
