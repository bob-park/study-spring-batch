package com.example.springbatchstep.ch03_jobstep;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JobStep
 *
 * <pre>
 *     - Job 에 속하는 Step 중 외부의 Job 을 포함하고 있는 Job
 *     - 외부의 Job 이 실패하면 해당 Step 이 실패하므로 결국 최종 기본 Job 도 실패
 *     - 모든 메타데이터는 기본 Job 과 외부 Job 별로 각각 저장된다.
 *     - 커다란 시스템을 작은 모듈로 쪼개고, Job 의 흐름를 관리하고 할 떄 사용할 수 있다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class JobStepConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job parentJob() {
        return jobBuilderFactory.get("parentJob")
            .incrementer(new RunIdIncrementer())
            .start(jobStep(null))
            .next(step2())
            .build();
    }

    @Bean
    public Step jobStep(JobLauncher jobLauncher) {
        return stepBuilderFactory.get("jobStep")
            .job(childJob()) // JobStep 내에서 실핼 될 Job 설정, JobStepBuilder 반환
            .launcher(jobLauncher) // Job 을 실행할 JobLauncher 설정
            .parametersExtractor(
                jobParametersExtractor()) // Step 의 ExecutionContext 를 job 이 실행되는데 필요한 JobParameters 로 변환
            .listener(new StepExecutionListener() {
                @Override
                public void beforeStep(StepExecution stepExecution) {
                    stepExecution.getExecutionContext().putString("name", "user1");
                }

                @Override
                public ExitStatus afterStep(StepExecution stepExecution) {
                    return null;
                }
            })
            .build();
    }

    /**
     * ExecutionContext 안에 있는 값을 key - value 로 하여 job parameter 로 생성한다.
     *
     * @return
     */
    private DefaultJobParametersExtractor jobParametersExtractor() {

        DefaultJobParametersExtractor extractor = new DefaultJobParametersExtractor();
        extractor.setKeys(
            new String[]{"name"}); // ExecutionContext 에서 name 의 key 를 찾아 JobParameter 로 추출한다.

        return extractor;
    }

    @Bean
    public Job childJob() {
        return jobBuilderFactory.get("childJob")
            .start(step1())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .tasklet((contribution, chunkContext) -> {
                log.info("step1 was executed");
//                throw new RuntimeException("step1 was failed.");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet((contribution, chunkContext) -> {
                log.info("step2 was execute");
                throw new RuntimeException("step2 was failed.");
//                return RepeatStatus.FINISHED;
            })
            .build();
    }


}
