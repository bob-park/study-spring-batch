package com.example.springbatch.configure;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobParameterConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
            .start(step1())
            .next(step2())
            .build();
    }

    @Bean
    public Step step1() {
        // Job Parameter 는 Step 단계에서 참조할 수 있다.
        return stepBuilderFactory.get("step1")
            .tasklet((contribution, chunkContext) -> {

                // contribution 으로 job parameter 가져오기 - 실사용시
                JobParameters jobParameters = contribution.getStepExecution().getJobParameters();

                jobParameters.getString("name");
                jobParameters.getLong("seq");
                jobParameters.getDate("date");
                jobParameters.getDouble("age");

                // chunk context 로 job parameter 가져오기 - 값 확인용
                Map<String, Object> jobParamMap = chunkContext.getStepContext()
                    .getJobParameters();

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
