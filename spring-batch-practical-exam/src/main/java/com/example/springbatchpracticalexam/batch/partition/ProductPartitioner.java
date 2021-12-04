package com.example.springbatchpracticalexam.batch.partition;

import com.example.springbatchpracticalexam.batch.domain.ProductVO;
import com.example.springbatchpracticalexam.batch.job.api.QueryGenerator;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

@RequiredArgsConstructor
public class ProductPartitioner implements Partitioner {

    private final DataSource dataSource;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        ProductVO[] productList = QueryGenerator.getProductList(dataSource);

        Map<String, ExecutionContext> result = new HashMap<>();

        int number = 0;

        for (ProductVO productVO : productList) {
            ExecutionContext value = new ExecutionContext();

            value.put("product", productVO);

            result.put("partition-" + number, value);

            number++;
        }

        return result;
    }
}
