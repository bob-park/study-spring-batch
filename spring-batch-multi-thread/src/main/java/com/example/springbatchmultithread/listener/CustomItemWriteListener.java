package com.example.springbatchmultithread.listener;

import com.example.springbatchmultithread.model.Customer;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;

@Slf4j
public class CustomItemWriteListener implements ItemWriteListener<Customer> {

    @Override
    public void beforeWrite(List<? extends Customer> items) {

    }

    @Override
    public void afterWrite(List<? extends Customer> items) {
        log.info("thread={}, write item size={}", Thread.currentThread().getName(), items.size());
    }

    @Override
    public void onWriteError(Exception exception, List<? extends Customer> items) {

    }
}
