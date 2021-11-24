package com.example.springbatchchunk.writer.ch06_itemwriteradapter.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomService<T> {

    private int cnt = 0;

    public void customWrite(T item) {
        log.info("item={}", item);
    }

}
