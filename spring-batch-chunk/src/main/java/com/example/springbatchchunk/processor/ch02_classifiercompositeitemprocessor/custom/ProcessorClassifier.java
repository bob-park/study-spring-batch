package com.example.springbatchchunk.processor.ch02_classifiercompositeitemprocessor.custom;

import com.example.springbatchchunk.processor.ch02_classifiercompositeitemprocessor.model.ProcessorInfo;
import java.util.HashMap;
import java.util.Map;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.classify.Classifier;

public class ProcessorClassifier<C, T> implements Classifier<C, T> {

    private final Map<Long, ItemProcessor<ProcessorInfo, ProcessorInfo>> processorMap = new HashMap<>();

    @Override
    public T classify(C classifiable) {
        return (T) processorMap.get(((ProcessorInfo) classifiable).getId());
    }

    public ProcessorClassifier<C, T> add(Long key,
        ItemProcessor<ProcessorInfo, ProcessorInfo> itemProcessor) {
        processorMap.put(key, itemProcessor);
        return this;
    }


}
