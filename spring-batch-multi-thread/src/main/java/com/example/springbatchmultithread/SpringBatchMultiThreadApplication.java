package com.example.springbatchmultithread;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchMultiThreadApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchMultiThreadApplication.class, args);
    }

}
