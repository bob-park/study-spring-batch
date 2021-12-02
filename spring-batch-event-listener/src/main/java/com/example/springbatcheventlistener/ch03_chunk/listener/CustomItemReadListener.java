package com.example.springbatcheventlistener.ch03_chunk.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterChunkError;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j
public class CustomItemReadListener implements ItemReadListener<Integer> {

    @Override
    public void beforeRead() {
        log.info(" >> before read");
    }

    @Override
    public void afterRead(Integer item) {
        log.info(" >> after read");
    }

    @Override
    public void onReadError(Exception ex) {
        log.info(" >> read error");
    }
}
