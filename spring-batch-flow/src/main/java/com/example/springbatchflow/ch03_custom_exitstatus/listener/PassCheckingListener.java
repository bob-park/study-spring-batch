package com.example.springbatchflow.ch03_custom_exitstatus.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;

public class PassCheckingListener implements
    org.springframework.batch.core.StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        String exitCode = stepExecution.getExitStatus().getExitCode();

        // ExitStatus 가 FAILED 가 아닌 경우
        if (!exitCode.equals(ExitStatus.FAILED.getExitCode())) {
            return new ExitStatus("PASS");
        }

        return null;
    }
}
