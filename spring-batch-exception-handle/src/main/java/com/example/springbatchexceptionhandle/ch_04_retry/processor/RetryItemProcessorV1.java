package com.example.springbatchexceptionhandle.ch_04_retry.processor;

import com.example.springbatchexceptionhandle.ch_04_retry.exception.RetryableException;
import org.springframework.batch.item.ItemProcessor;

public class RetryItemProcessorV1 implements ItemProcessor<String, String> {

    private int cnt = 0;

    @Override
    public String process(String item) throws Exception {

        if (item.equals("2") || item.equals("3")) {
            cnt++;

            throw new RetryableException("failed cnt : " + cnt);
        }

        return item;
    }
}
