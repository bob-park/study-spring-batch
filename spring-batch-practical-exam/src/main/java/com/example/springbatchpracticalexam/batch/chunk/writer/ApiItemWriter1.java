package com.example.springbatchpracticalexam.batch.chunk.writer;

import com.example.springbatchpracticalexam.batch.domain.ApiRequestVO;
import com.example.springbatchpracticalexam.batch.domain.ApiResponseVO;
import com.example.springbatchpracticalexam.service.AbstractApiService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

@Slf4j
@RequiredArgsConstructor
public class ApiItemWriter1 implements ItemWriter<ApiRequestVO> {

    private final AbstractApiService apiService;

    @Override
    public void write(List<? extends ApiRequestVO> items) throws Exception {
        ApiResponseVO responseVO = apiService.service(items);

        log.info("responseVO={}", responseVO);
    }
}
