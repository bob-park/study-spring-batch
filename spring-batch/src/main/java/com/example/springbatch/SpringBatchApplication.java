package com.example.springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing // Batch 활성화
public class SpringBatchApplication {

    /**
     * ! 주의사항
     *
     * <pre>
     *     - Spring Batch 는 DB 에 스키마가 설정되어 있어야 동작한다.
     * </pre>
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringBatchApplication.class, args);
    }

}
