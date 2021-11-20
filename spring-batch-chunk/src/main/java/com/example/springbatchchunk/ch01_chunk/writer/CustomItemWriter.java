package com.example.springbatchchunk.ch01_chunk.writer;

import com.example.springbatchchunk.ch01_chunk.model.Customer;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

@Slf4j
public class CustomItemWriter implements ItemWriter<Customer> {

    @Override
    public void write(List<? extends Customer> items) throws Exception {
        items.forEach(item -> log.info("item={}", item));
    }
}
