package com.example.apiservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiRequestVO {

    private Long id;
    private ProductVO product;
}