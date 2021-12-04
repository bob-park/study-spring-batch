package com.example.springbatchpracticalexam.batch.job.api;

import com.example.springbatchpracticalexam.batch.domain.ProductVO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class QueryGenerator {

    public static ProductVO[] getProductList(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        List<ProductVO> list = jdbcTemplate.query("select type from product group by type",
            (rs, rowNum) -> ProductVO.builder().type(rs.getString("type")).build());

        return list.toArray(new ProductVO[]{});

    }

    public static Map<String, Object> getParameterForQuery(String parameter, String value) {

        Map<String, Object> parameters = new HashMap<>();

        parameters.put(parameter, value);

        return parameters;

    }

}
