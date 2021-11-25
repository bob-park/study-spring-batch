package com.example.springbatchchunk.processor.ch01_compositeItemprocessor.custom;

import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor1 implements ItemProcessor<String, String> {

    private int count = 0;

    @Override
    public String process(String item) throws Exception {

        count++;

        return (item + "-" + count).toUpperCase();
    }
}
