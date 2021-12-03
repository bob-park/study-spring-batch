package com.example.springbatchtest.ch02_extra.controller;

import com.example.springbatchtest.ch02_extra.dto.request.JobInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class JobController {

    private final JobRegistry jobRegistry;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;

    @PostMapping(path = "batch/start")
    public String start(@RequestBody JobInfo jobInfo) throws Exception {

        for (String jobName : jobRegistry.getJobNames()) {
            SimpleJob job = (SimpleJob) jobRegistry.getJob(jobName);

            log.info("jobName={}", job.getName());

            jobOperator.start(job.getName(), jobInfo.getId());
        }

        return "batch is started";

    }

    @PostMapping(path = "batch/stop")
    public String stop() throws Exception {

        for (String jobName : jobRegistry.getJobNames()) {
            SimpleJob job = (SimpleJob) jobRegistry.getJob(jobName);

            log.info("jobName={}", job.getName());

            for (JobExecution jobExecution : jobExplorer.findRunningJobExecutions(job.getName())) {
                jobOperator.stop(jobExecution.getJobId());
            }
        }

        return "batch is stopped.";

    }

    @PostMapping(path = "batch/restart")
    public String restart() throws Exception {

        for (String jobName : jobRegistry.getJobNames()) {
            SimpleJob job = (SimpleJob) jobRegistry.getJob(jobName);

            log.info("jobName={}", job.getName());

            JobInstance jobInstance = jobExplorer.getLastJobInstance(job.getName());

            if (jobInstance == null) {
                throw new RuntimeException("jobInstance is null.");
            }

            JobExecution jobExecution = jobExplorer.getLastJobExecution(jobInstance);

            if (jobExecution == null) {
                throw new RuntimeException("jobExecution is null.");
            }

            jobOperator.restart(jobExecution.getJobId());
        }

        return "batch is restarted.";

    }
}
