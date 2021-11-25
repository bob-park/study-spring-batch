package com.example.springbatchchunk.processor.ch02_classifiercompositeitemprocessor.model;

import lombok.Builder;
import lombok.Data;

@Data
public class ProcessorInfo {

    private Long id;

    @Builder
    protected ProcessorInfo(Long id) {
        this.id = id;
    }
}
