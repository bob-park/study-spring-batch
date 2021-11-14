package com.example.springbatch.step.executioncontext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExecutionContextTasklet1 implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws Exception {

        log.info("step1 was executed.");

        // contribution 에서 ExecutionContext 가져오기
        ExecutionContext jobExecutionContext = contribution.getStepExecution().getJobExecution()
            .getExecutionContext();

        ExecutionContext stepExecutionContext = contribution.getStepExecution()
            .getExecutionContext();

        // chunkContext 에서 ExecutionContext 가져오기
        String jobName = chunkContext.getStepContext().getStepExecution().getJobExecution()
            .getJobInstance().getJobName();
        String stepName = chunkContext.getStepContext().getStepName();

        // JobExecutionContext 에 jobName 을 저장하지 않는 경우
        if (jobExecutionContext.get("jobName") == null) {
            jobExecutionContext.put("jobName", jobName);
        }

        // StepExecutionContext 에 stepName 을 저장하지 않는 경우
        if (stepExecutionContext.get("stepName") == null) {
            stepExecutionContext.put("stepName", stepName);
        }

        log.info("jobName : {}", jobExecutionContext.get("jobName"));
        log.info("stepName : {}", stepExecutionContext.get("stepName"));

        return RepeatStatus.FINISHED;
    }
}
