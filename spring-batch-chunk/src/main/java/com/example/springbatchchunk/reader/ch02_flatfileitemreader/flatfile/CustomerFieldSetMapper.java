package com.example.springbatchchunk.reader.ch02_flatfileitemreader.flatfile;

import com.example.springbatchchunk.reader.ch02_flatfileitemreader.model.Customer;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {

    @Override
    public Customer mapFieldSet(FieldSet fieldSet) throws BindException {

        Customer customer = new Customer();

        customer.setName(fieldSet.readString(0));
        customer.setAge(fieldSet.readInt(1));
        customer.setYear(fieldSet.readInt(2));

        return customer;
    }
}
