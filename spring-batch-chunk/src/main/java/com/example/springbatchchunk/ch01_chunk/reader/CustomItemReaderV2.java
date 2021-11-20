package com.example.springbatchchunk.ch01_chunk.reader;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

@Slf4j
public class CustomItemReaderV2 implements ItemStreamReader<String> {

    private final List<String> items;
    private int index = -1;
    private boolean restart = false;

    public CustomItemReaderV2(List<String> items) {
        this.items = new ArrayList<>(items);
        this.index = 0;
    }

    @Override
    public String read()
        throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        String item = null;

        if (index < items.size()) {
            item = items.get(index++);
        }

        // index 6 이고 재시작 상태가 아닌 경우 throw exception
        if (index == 6 && !restart) {
            throw new RuntimeException("Restart is required.");
        }

        return item;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        log.info("CustomItemReaderV2 open...");
        if (executionContext.containsKey("index")) {
            this.index = executionContext.getInt("index");
            this.restart = true;
        } else {
            this.index = 0;

            executionContext.put("index", index);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        // chunk 가 종료될때 마다 실행
        log.info("CustomItemReaderV2 update...");
        executionContext.put("index", index);
    }

    @Override
    public void close() throws ItemStreamException {
        log.info("CustomItemReaderV2 close...");
    }
}
