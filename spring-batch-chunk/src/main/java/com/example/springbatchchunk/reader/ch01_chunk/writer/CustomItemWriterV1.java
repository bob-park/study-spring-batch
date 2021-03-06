package com.example.springbatchchunk.reader.ch01_chunk.writer;

import com.example.springbatchchunk.reader.ch01_chunk.model.Customer;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

@Slf4j
public class CustomItemWriterV1 implements ItemWriter<Customer> {

    @Override
    public void write(List<? extends Customer> items) throws Exception {
        items.forEach(item -> log.info("item={}", item));
    }
}
