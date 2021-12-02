package com.example.springbatcheventlistener.ch03_chunk.listener;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;

@Slf4j
public class CustomItemWriterListener implements ItemWriteListener<String> {

    @Override
    public void beforeWrite(List<? extends String> items) {
        log.info(" >> before write");
    }

    @Override
    public void afterWrite(List<? extends String> items) {
        log.info(" >> after write");
    }

    @Override
    public void onWriteError(Exception exception, List<? extends String> items) {
        log.info(" >> write error");
    }
}
