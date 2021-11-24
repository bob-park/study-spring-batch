package com.example.springbatchchunk.writer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerV2 {

    private Long id;
    private String firstName;
    private String lastName;
    private String birthDate;

}
