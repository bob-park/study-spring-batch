package com.example.springbatchpracticalexam.batch.chunk.processor;

import com.example.springbatchpracticalexam.batch.domain.ApiRequestVO;
import com.example.springbatchpracticalexam.batch.domain.ProductVO;
import org.springframework.batch.item.ItemProcessor;

public class ApiItemProcessor2 implements ItemProcessor<ProductVO, ApiRequestVO> {

    @Override
    public ApiRequestVO process(ProductVO item) throws Exception {
        return ApiRequestVO.builder()
            .id(item.getId())
            .product(item)
            .build();
    }
}
