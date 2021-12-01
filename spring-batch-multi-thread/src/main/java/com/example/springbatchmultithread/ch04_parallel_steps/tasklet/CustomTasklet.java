package com.example.springbatchmultithread.ch04_parallel_steps.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class CustomTasklet implements Tasklet {

    private long sum;

    private final Object lock = new Object();

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
        throws Exception {

        /*
         sum 을 thread 간 공유하기 떄문에 정확하게 계산되지 않음

         ! Parallel Step 을 사용하여 Thread 간 공유할 수 있는 값이 존재할 경우 반드시 synchronized 처리를 해주어야 한다.

         ! 단, synchronized 를 사용할 경우 성능이 매우 저하될 수 있음
         */
        // synchronized 사용하여 동기화
        synchronized (lock) {
            for (int i = 0; i < 100_000_000; i++) {
                sum++;
            }

            log.info("{} has bean executed on thread {}",
                chunkContext.getStepContext().getStepName(),
                Thread.currentThread().getName());

            log.info("sum={}", sum);
        }

        return RepeatStatus.FINISHED;
    }
}
