package com.example.springbatchchunk.writer.ch05_jpaitemwriter.processor;

import com.example.springbatchchunk.reader.ch05_db.entity.CustomerEntity;
import com.example.springbatchchunk.writer.model.CustomerV2;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.batch.item.ItemProcessor;

public class CustomItemProcessor implements ItemProcessor<CustomerV2, CustomerEntity> {

    @Override
    public CustomerEntity process(CustomerV2 item) throws Exception {

        return new CustomerEntity(item.getId(),
            item.getFirstName(),
            item.getLastName(),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));

    }
}
