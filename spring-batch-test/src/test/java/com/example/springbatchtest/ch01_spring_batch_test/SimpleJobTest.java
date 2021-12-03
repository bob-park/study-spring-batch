package com.example.springbatchtest.ch01_spring_batch_test;

import com.example.springbatchtest.config.TestBatchConfiguration;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Spring Batch Test
 *
 * <pre>
 *      - dependency 추가 필요
 *          - spring batch 4.1.x 이상(Spring Boot 2.1) 기준
 *          - spring-batch-test
 *
 *      - @SpringBatchTest
 *          - 자동으로 ApplicationContext 에 테스트에 필요한 여러 Util Bean 을 등록해주는 Annotation
 *
 *          - JobLauncherTestUtils
 *              - launchJob(), launchStep() 과 같은 Spring Batch Test 에 필요한 유틸성 메소드 지원
 *
 *              - void setJob(Job job)
 *                  - 실행할 Job 을 자동으로 주입 받음
 *                  - 한개의 Job 만 받을 수 있음(Job 설정 클래스를 한개만 지정해야함)
 *
 *              - JobExecution launchJob(JobParameters)
 *                  - Job 을 실행시키고 JobExecution 을 반환
 *
 *              - JobExecution launchStep(String stepName)
 *                  - Step 을 실행시키고 JobExecution 을 반환
 *
 *          - JobRepositoryTestUtils
 *              - JobRepository 를 사용해서 JobExecution 을 생성 및 삭제 기능 메소드 지원
 *
 *              - List<JobExecution> createJobExecutions(jobName, stepNames, count)
 *                  - JobExecution 생성 - job 이름, Step 이름, 생성 개수
 *
 *              - void removeJobExecution(Collection<JobExecution>)
 *                  - JobExecution 삭제 - JobExecution 목록
 *
 *          - StepScopeTestExecutionListener
 *              -  @StepScope Context 를 생성해주며 해당 Context 를 통해 JobParameter 등을 단위 테스트에서 DI 받을 수 있다.
 *          - JobScopeTestExecutionListener
 *              -  @JobScope Context 를 생성해주며 해당 Context 를 통해 JobParameter 등을 단위 테스트에서 DI 받을 수 있다.
 * </pre>
 */
@ActiveProfiles("mysql")
@Slf4j
@SpringBatchTest // JobLauncherTestUtils, JobRepositoryTestUtils 등을 제공하는 Annotation
// Job 설정 Class 지정, 통합 테스트를 위한 여러 의존성 bean 들을 주입받기 위한 Annotation
@SpringBootTest(classes = {SimpleJobConfiguration.class, TestBatchConfiguration.class})
class SimpleJobTest {

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void simpleJob_test() throws Exception {
        // given
        JobParameters jobParameters = new JobParametersBuilder()
            .addString("name", "user1")
            .addLong("date", new Date().getTime())
            .toJobParameters();

        // when
//        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        JobExecution jobExecution1 = jobLauncherTestUtils.launchStep("step1");

        StepExecution stepExecution = ((List<StepExecution>) jobExecution1.getStepExecutions())
            .get(0);

        //then
//        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
//        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

        /*
         ! commit count 가 chunk Size 보다 1이 더 큰 이유
         - 일괄 작업의 최종 상태를 update 하기 때문에 1이 더 추가된다.
         */
        assertThat(stepExecution.getCommitCount()).isEqualTo(6);
        assertThat(stepExecution.getReadCount()).isEqualTo(10);
        assertThat(stepExecution.getWriteCount()).isEqualTo(10);


    }

    @AfterEach
    public void clear() {
        jdbcTemplate.execute("delete from customer2");
    }

}
