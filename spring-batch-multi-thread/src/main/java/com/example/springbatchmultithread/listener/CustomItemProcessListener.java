package com.example.springbatchmultithread.listener;

import com.example.springbatchmultithread.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;

@Slf4j
public class CustomItemProcessListener implements ItemProcessListener<Customer, Customer> {

    @Override
    public void beforeProcess(Customer item) {

    }

    @Override
    public void afterProcess(Customer item, Customer result) {
        log.info("thread={}, process item={}", Thread.currentThread().getName(), item.getId());
    }

    @Override
    public void onProcessError(Customer item, Exception e) {

    }
}
