package com.example.springbatchpracticalexam.service;

import com.example.springbatchpracticalexam.batch.domain.ApiInfo;
import com.example.springbatchpracticalexam.batch.domain.ApiRequestVO;
import com.example.springbatchpracticalexam.batch.domain.ApiResponseVO;
import java.io.IOException;
import java.util.List;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

public abstract class AbstractApiService {

    public ApiResponseVO service(List<? extends ApiRequestVO> list) {

        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

        RestTemplate restTemplate = restTemplateBuilder.errorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {

            }
        }).build();

        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        ApiInfo apiInfo = ApiInfo.builder().apiRequestList(list).build();

        return doApiService(restTemplate, apiInfo);

    }

    protected abstract ApiResponseVO doApiService(RestTemplate restTemplate, ApiInfo apiInfo);

}
