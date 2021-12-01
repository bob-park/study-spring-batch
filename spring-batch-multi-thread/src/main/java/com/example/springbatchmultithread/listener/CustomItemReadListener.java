package com.example.springbatchmultithread.listener;

import com.example.springbatchmultithread.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;

@Slf4j
public class CustomItemReadListener implements ItemReadListener<Customer> {

    @Override
    public void beforeRead() {

    }

    @Override
    public void afterRead(Customer item) {
        log.info("thread={}, read item={}", Thread.currentThread().getName(), item.getId());
    }

    @Override
    public void onReadError(Exception ex) {

    }
}
