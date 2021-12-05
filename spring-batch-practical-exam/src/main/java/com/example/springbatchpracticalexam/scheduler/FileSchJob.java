package com.example.springbatchpracticalexam.scheduler;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileSchJob extends QuartzJobBean {

    private final Job fileJob;

    private final JobLauncher jobLauncher;

    private final JobExplorer jobExplorer;

    @SneakyThrows
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // 실제 Schedule 의 대한 작업을 정의한다.

        String requestDate = (String) context.getJobDetail().getJobDataMap().get("requestDate");

        // 이전 데이터 추가 확인
        // 추가된 데이터인 경우 실행 X

        // 해당 Job Name 으로 가지고 있는 JobInstance 총 개수
        int jobInstanceCount = jobExplorer.getJobInstanceCount(fileJob.getName());

        // 해당 JobName 으로 가지고 있는 Instance start, end 까지 가져오기
        List<JobInstance> jobInstances = jobExplorer.getJobInstances(fileJob.getName(), 0,
            jobInstanceCount);

        // 해당과 동일한 날짜의 작업은 실행시키지 않고 예외를 발생시킨다.
        if (!jobInstances.isEmpty()) {
            for (JobInstance jobInstance : jobInstances) {
                List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);

                List<JobExecution> jobExecutionList = jobExecutions.stream()
                    .filter(jobExecution ->
                        requestDate.equals(
                            jobExecution.getJobParameters().getString("requestDate")))
                    .collect(Collectors.toList());

                if (!jobExecutionList.isEmpty()) {
                    throw new JobExecutionException(requestDate + " already exist.");
                }

            }
        }

        JobParameters jobParameters = new JobParametersBuilder()
            .addLong("id", new Date().getTime())
            .addString("requestDate", requestDate)
            .toJobParameters();

        jobLauncher.run(fileJob, jobParameters);

    }
}
