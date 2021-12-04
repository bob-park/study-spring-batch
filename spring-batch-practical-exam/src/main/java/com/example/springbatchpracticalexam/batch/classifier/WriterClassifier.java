package com.example.springbatchpracticalexam.batch.classifier;

import com.example.springbatchpracticalexam.batch.domain.ApiRequestVO;
import java.util.HashMap;
import java.util.Map;
import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

public class WriterClassifier<C, T> implements Classifier<C, T> {

    private Map<String, ItemWriter<ApiRequestVO>> processorMap = new HashMap<>();

    @Override
    public T classify(C classifiable) {

        return (T) processorMap.get(((ApiRequestVO) classifiable).getProduct().getType());
    }

    public void setWriterMap(Map<String, ItemWriter<ApiRequestVO>> processorMap) {
        this.processorMap = processorMap;
    }
}
