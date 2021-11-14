package com.example.springbatch.step.executioncontext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExecutionContextTasklet4 implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws Exception {

        log.info("step4 was executed.");

        Object name = chunkContext.getStepContext().getStepExecution().getJobExecution()
            .getExecutionContext()
            .get("name");

        log.info("name : {}", name);

        return RepeatStatus.FINISHED;
    }
}