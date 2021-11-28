package com.example.springbatchexceptionhandle.ch_04_retry.processor;

import com.example.springbatchexceptionhandle.ch_04_retry.exception.RetryableException;
import org.springframework.batch.item.ItemProcessor;

public class RetryItemProcessor implements ItemProcessor<String, String> {

    private int cnt = 0;

    @Override
    public String process(String item) throws Exception {

        cnt++;

        throw new RetryableException("Retry : " + cnt);

//        return null;
    }
}
