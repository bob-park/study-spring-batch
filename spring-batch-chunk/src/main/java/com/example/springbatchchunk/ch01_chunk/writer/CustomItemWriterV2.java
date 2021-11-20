package com.example.springbatchchunk.ch01_chunk.writer;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

@Slf4j
public class CustomItemWriterV2 implements ItemStreamWriter<String> {

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        log.info("CustomItemWriterV2 open...");
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        log.info("CustomItemWriterV2 update...");
    }

    @Override
    public void close() throws ItemStreamException {
        log.info("CustomItemWriterV2 close...");
    }

    @Override
    public void write(List<? extends String> items) throws Exception {
        items.forEach(item -> log.info("item={}", item));
    }
}
