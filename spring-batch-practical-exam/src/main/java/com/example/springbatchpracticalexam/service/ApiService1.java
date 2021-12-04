package com.example.springbatchpracticalexam.service;

import com.example.springbatchpracticalexam.batch.domain.ApiInfo;
import com.example.springbatchpracticalexam.batch.domain.ApiResponseVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService1 extends AbstractApiService {

    @Override
    public ApiResponseVO doApiService(RestTemplate restTemplate, ApiInfo apiInfo) {
        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:8081/api/product/1", apiInfo, String.class);

        HttpStatus httpStatus = response.getStatusCode();

        return ApiResponseVO.builder()
            .status(httpStatus.value())
            .message(response.getBody())
            .build();
    }
}
