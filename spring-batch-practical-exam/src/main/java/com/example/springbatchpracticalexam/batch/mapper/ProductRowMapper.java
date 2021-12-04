package com.example.springbatchpracticalexam.batch.mapper;

import com.example.springbatchpracticalexam.batch.domain.ProductVO;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class ProductRowMapper implements RowMapper<ProductVO> {

    @Override
    public ProductVO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ProductVO.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .price(rs.getLong("price"))
            .type(rs.getString("type"))
            .build();
    }
}
