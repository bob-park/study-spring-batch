package com.example.springbatchchunk.reader.ch01_chunk.processor;

import com.example.springbatchchunk.reader.ch01_chunk.model.Customer;
import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer customer) throws Exception {

        customer.setName(customer.getName().toUpperCase());

        return customer;
    }
}
