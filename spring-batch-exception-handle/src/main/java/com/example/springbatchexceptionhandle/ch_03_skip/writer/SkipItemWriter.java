package com.example.springbatchexceptionhandle.ch_03_skip.writer;

import com.example.springbatchexceptionhandle.ch_03_skip.exception.SkippableException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

@Slf4j
public class SkipItemWriter implements ItemWriter<String> {

    private int count = 0;

    @Override
    public void write(List<? extends String> items) throws Exception {

        for (String item : items) {
            count++;

            log.info("ItemWriter={}", item);

            if (item.equals("-12")) {
                throw new SkippableException("Writer failed count : " + count);
            }

        }

    }
}
