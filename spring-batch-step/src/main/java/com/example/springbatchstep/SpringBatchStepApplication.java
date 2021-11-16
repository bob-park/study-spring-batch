package com.example.springbatchstep;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchStepApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchStepApplication.class, args);
    }

}
