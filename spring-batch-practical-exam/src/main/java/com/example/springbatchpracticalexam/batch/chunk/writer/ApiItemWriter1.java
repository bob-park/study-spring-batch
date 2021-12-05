package com.example.springbatchpracticalexam.batch.chunk.writer;

import com.example.springbatchpracticalexam.batch.domain.ApiRequestVO;
import com.example.springbatchpracticalexam.batch.domain.ApiResponseVO;
import com.example.springbatchpracticalexam.service.AbstractApiService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.core.io.FileSystemResource;

@Slf4j
@RequiredArgsConstructor
public class ApiItemWriter1 extends FlatFileItemWriter<ApiRequestVO> {

    private final AbstractApiService apiService;

    @Override
    public void write(List<? extends ApiRequestVO> items) throws Exception {
        ApiResponseVO responseVO = apiService.service(items);

        log.info("responseVO={}", responseVO);

        items.forEach(item -> item.setResponseVO(responseVO));

        super.setResource(new FileSystemResource(
            "/Users/hwpark/Documents/study/spring-batch-workspace/study-spring-batch/spring-batch-practical-exam/src/main/resources/product1.txt"));
        super.open(new ExecutionContext());
        super.setAppendAllowed(true);
        super.setLineAggregator(new DelimitedLineAggregator<>());
        super.write(items);

    }
}
