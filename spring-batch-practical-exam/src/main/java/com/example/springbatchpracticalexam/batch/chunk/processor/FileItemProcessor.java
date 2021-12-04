package com.example.springbatchpracticalexam.batch.chunk.processor;

import com.example.springbatchpracticalexam.batch.domain.Product;
import com.example.springbatchpracticalexam.batch.domain.ProductVO;
import org.springframework.batch.item.ItemProcessor;

public class FileItemProcessor implements ItemProcessor<ProductVO, Product> {

    @Override
    public Product process(ProductVO item) throws Exception {

        return Product.builder()
            .id(item.getId())
            .name(item.getName())
            .price(item.getPrice())
            .type(item.getType())
            .build();
    }
}
