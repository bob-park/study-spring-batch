package com.example.springbatchpracticalexam.batch.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponseVO {

    private Integer status;
    private String message;

}
