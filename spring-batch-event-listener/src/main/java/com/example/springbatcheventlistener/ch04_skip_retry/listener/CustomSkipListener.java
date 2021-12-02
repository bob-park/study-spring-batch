package com.example.springbatcheventlistener.ch04_skip_retry.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;

@Slf4j
public class CustomSkipListener implements SkipListener<Integer, String> {

    @Override
    public void onSkipInRead(Throwable t) {
        log.info(">> onSkipRead : {}", t.getMessage());
    }

    @Override
    public void onSkipInWrite(String item, Throwable t) {
        log.info(">> onSkipInWrite : item={}, message={}", item, t.getMessage());
    }

    @Override
    public void onSkipInProcess(Integer item, Throwable t) {
        log.info(">> onSkipInProcess : item={}, message={}", item, t.getMessage());
    }
}
