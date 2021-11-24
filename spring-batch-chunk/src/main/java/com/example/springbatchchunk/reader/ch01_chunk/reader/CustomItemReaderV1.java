package com.example.springbatchchunk.reader.ch01_chunk.reader;

import com.example.springbatchchunk.reader.ch01_chunk.model.Customer;
import java.util.ArrayList;
import java.util.List;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class CustomItemReaderV1 implements ItemReader<Customer> {

    private List<Customer> list;

    public CustomItemReaderV1(List<Customer> list) {
        this.list = new ArrayList<>(list);
    }

    @Override
    public Customer read()
        throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        if (!list.isEmpty()) {
            return list.remove(0);
        }

        return null;
    }
}
