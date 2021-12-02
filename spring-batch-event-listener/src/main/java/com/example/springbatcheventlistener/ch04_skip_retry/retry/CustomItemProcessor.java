package com.example.springbatcheventlistener.ch04_skip_retry.retry;

import com.example.springbatcheventlistener.ch04_skip_retry.exception.CustomRetryException;
import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor implements ItemProcessor<Integer, String> {

    private int count = 0;

    @Override
    public String process(Integer item) throws Exception {

        if (count < 2) {
            count++;

            if (count % 2 == 1) {
                throw new CustomRetryException("process retried.");
            }
        }

        return String.format("item-%d", count);
    }
}
