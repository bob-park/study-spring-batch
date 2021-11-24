package com.example.springbatchchunk.writer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerV1 {

    private Long id;
    private String name;
    private Integer age;

}
