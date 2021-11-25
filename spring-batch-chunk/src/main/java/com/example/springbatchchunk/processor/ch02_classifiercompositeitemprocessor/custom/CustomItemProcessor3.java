package com.example.springbatchchunk.processor.ch02_classifiercompositeitemprocessor.custom;

import com.example.springbatchchunk.processor.ch02_classifiercompositeitemprocessor.model.ProcessorInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class CustomItemProcessor3 implements ItemProcessor<ProcessorInfo, ProcessorInfo> {

    @Override
    public ProcessorInfo process(ProcessorInfo item) throws Exception {

        log.info("CustomItemProcessor3 execute");

        return item;
    }
}
