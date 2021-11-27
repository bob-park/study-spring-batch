package com.example.springbatchexceptionhandle.ch_03_skip.processor;

import com.example.springbatchexceptionhandle.ch_03_skip.exception.SkippableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class SkipItemProcessor implements ItemProcessor<String, String> {

    private int count = 0;

    @Override
    public String process(String item) throws Exception {

        count++;

        log.info("ItemProcess={}", item);

        if (item.equals("6") || item.equals("7")) {
            throw new SkippableException("Process failed count : " + count);
        }

        return String.valueOf(Integer.parseInt(item) * -1);
    }
}
