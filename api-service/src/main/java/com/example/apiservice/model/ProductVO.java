package com.example.apiservice.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductVO {
    private Long id;
    private String name;
    private Long price;
    private String type;

    @Builder
    private ProductVO(Long id, String name, Long price, String type) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
    }
}
