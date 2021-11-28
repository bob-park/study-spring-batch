package com.example.springbatchexceptionhandle;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchExceptionHandleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchExceptionHandleApplication.class, args);
    }

}
