package com.example.springbatchflow;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchFlowApplication.class, args);
    }

}
