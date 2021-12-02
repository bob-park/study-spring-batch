package com.example.springbatcheventlistener.ch03_chunk.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterChunkError;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j
public class CustomItemProcessListener implements ItemProcessListener<Integer, String> {

    @Override
    public void beforeProcess(Integer item) {
        log.info(" >> before process");
    }

    @Override
    public void afterProcess(Integer item, String result) {
        log.info(" >> after process");
    }

    @Override
    public void onProcessError(Integer item, Exception e) {
        log.info(" >> process error");
    }
}
