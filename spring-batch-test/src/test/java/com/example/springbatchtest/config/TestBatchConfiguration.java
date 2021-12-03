package com.example.springbatchtest.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
/*
 * 테스트 시 Batch 환경 및 설정 초기화를 자동 구동하기 위한 Annotation
 * Test Class 마다 선언하지 않고 공통으로 사용하기 위함
 */
@EnableBatchProcessing
public class TestBatchConfiguration {

}
