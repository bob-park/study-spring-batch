package com.example.apiservice.controller;

import com.example.apiservice.model.ApiInfo;
import com.example.apiservice.model.ApiRequestVO;
import com.example.apiservice.model.ProductVO;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ApiController {

    @PostMapping(path = "api/product/1")
    public String product1(@RequestBody ApiInfo apiInfo) {

        List<ProductVO> productList = apiInfo.getApiRequestList().stream()
            .map(ApiRequestVO::getProduct)
            .collect(Collectors.toList());

        log.info("productList={}", productList);

        return "product1 was successfully processed.";
    }

    @PostMapping(path = "api/product/2")
    public String product2(@RequestBody ApiInfo apiInfo) {

        List<ProductVO> productList = apiInfo.getApiRequestList().stream()
            .map(ApiRequestVO::getProduct)
            .collect(Collectors.toList());

        log.info("productList={}", productList);

        return "product2 was successfully processed.";
    }

    @PostMapping(path = "api/product/3")
    public String product3(@RequestBody ApiInfo apiInfo) {

        List<ProductVO> productList = apiInfo.getApiRequestList().stream()
            .map(ApiRequestVO::getProduct)
            .collect(Collectors.toList());

        log.info("productList={}", productList);

        return "product3 was successfully processed.";
    }

}
