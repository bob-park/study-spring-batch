package com.example.springbatchexceptionhandle.ch_04_retry.processor;

import com.example.springbatchexceptionhandle.ch_04_retry.exception.RetryableException;
import com.example.springbatchexceptionhandle.ch_04_retry.model.Customer;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.classify.Classifier;
import org.springframework.retry.support.DefaultRetryState;
import org.springframework.retry.support.RetryTemplate;

@RequiredArgsConstructor
public class RetryItemProcessorV2 implements ItemProcessor<String, Customer> {

    private final RetryTemplate retryTemplate;

    private int cnt = 0;

    @Override
    public Customer process(String item) throws Exception {

        Classifier<Throwable, Boolean> rollbackClassifier = new BinaryExceptionClassifier(
            List.of(RetryableException.class), true);


        /*
         ! Chunk 의 맨 앞으로 가지 않는다.

         * Retry API 로 설정 할 경우 Chunk 단계에서 맨앞으로 이동하여 Chunk 의 처음부터 실행되지만, RetryTemplate 인 경우 Chunk 의 맨앞으로 가지 않고, 현재 아이템을 반복한다.
         * 만일, 재시도 후 limit 을 초과하여 exception 이 발생할 경우, Skip 을 확인 후 RecoveryCallback 이 실행된다. -> chunk 가 실패되지 않는다.

         ! RetryState 를 설정하게 되면, Chunk 의 맨앞으로 가서 다시 처리 및 Skip 을 요구하게 된다.
         */
        return retryTemplate.execute(
            context -> {
                if (item.equals("1") || item.equals("2")) {
                    cnt++;
                    throw new RetryableException("failed cnt : " + cnt);
                }

                return new Customer(item);
            },
            context -> new Customer("recovery-" + item),
            // Retry 시 Rollback 에 대한 예외가 발생하면, 항상 status 가 true 로 설정하여 Chunk 의 처음으로 돌아가는 설정
            // 이 설정을 하게되면, 항상 Skip 을 요구한다. - Skip 이 없는 경우 예외가 발생
            new DefaultRetryState(item, rollbackClassifier));
    }
}
