package com.example.springbatcheventlistener.ch04_skip_retry.retry;

import com.example.springbatcheventlistener.ch04_skip_retry.exception.CustomRetryException;
import com.example.springbatcheventlistener.ch04_skip_retry.exception.CustomSkipException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

@Slf4j
public class CustomItemWriter implements ItemWriter<String> {

    private int count = 0;

    @Override
    public void write(List<? extends String> items) throws Exception {

        for (String item : items) {
            if (count < 2) {
                count++;

                if (count % 2 == 1) {
                    throw new CustomRetryException("process retried.");
                }
            }

            log.info("write : {}", item);
        }
    }
}
