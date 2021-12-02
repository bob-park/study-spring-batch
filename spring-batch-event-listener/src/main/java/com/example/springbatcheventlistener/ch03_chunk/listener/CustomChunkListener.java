package com.example.springbatcheventlistener.ch03_chunk.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.AfterChunkError;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j
public class CustomChunkListener {

    @BeforeChunk
    public void beforeChunk(ChunkContext context) {
        log.info(" >> before chunk");
    }

    @AfterChunk
    public void afterChunk(ChunkContext context) {
        log.info(" >> after chunk");
    }

    @AfterChunkError
    public void afterChunkError(ChunkContext context) {
        log.info(" >> after chunk error");
    }
}
