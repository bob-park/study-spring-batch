package com.example.springbatchmultithread.ch02_async.mapper;

import com.example.springbatchmultithread.ch02_async.model.Customer;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class CustomerRowMapper implements RowMapper<Customer> {

    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new Customer(rs.getLong("id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("birth_date"));
    }
}
