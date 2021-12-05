package com.example.springbatchpracticalexam.batch.job.api;

import com.example.springbatchpracticalexam.batch.chunk.processor.ApiItemProcessor1;
import com.example.springbatchpracticalexam.batch.chunk.processor.ApiItemProcessor2;
import com.example.springbatchpracticalexam.batch.chunk.processor.ApiItemProcessor3;
import com.example.springbatchpracticalexam.batch.chunk.writer.ApiItemWriter1;
import com.example.springbatchpracticalexam.batch.chunk.writer.ApiItemWriter2;
import com.example.springbatchpracticalexam.batch.chunk.writer.ApiItemWriter3;
import com.example.springbatchpracticalexam.batch.classifier.ProcessorClassifier;
import com.example.springbatchpracticalexam.batch.classifier.WriterClassifier;
import com.example.springbatchpracticalexam.batch.domain.ApiRequestVO;
import com.example.springbatchpracticalexam.batch.domain.ProductVO;
import com.example.springbatchpracticalexam.batch.partition.ProductPartitioner;
import com.example.springbatchpracticalexam.service.ApiService1;
import com.example.springbatchpracticalexam.service.ApiService2;
import com.example.springbatchpracticalexam.service.ApiService3;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.support.builder.ClassifierCompositeItemProcessorBuilder;
import org.springframework.batch.item.support.builder.ClassifierCompositeItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@RequiredArgsConstructor
@Configuration
public class ApiStepConfiguration {

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    private final ApiService1 apiService1;
    private final ApiService2 apiService2;
    private final ApiService3 apiService3;

    private static final int CHUNK_SIZE = 10;

    @Bean
    public Step apiMasterStep() {
        return stepBuilderFactory.get("apiMasterStep")
            .partitioner(apiSlaveStep().getName(), partitioner())
            .step(apiSlaveStep())
            .gridSize(3)
            .taskExecutor(taskExecutor())
            .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(3);
        taskExecutor.setMaxPoolSize(6);
        taskExecutor.setThreadNamePrefix("api-thread-");

        return taskExecutor;
    }

    @Bean
    public Step apiSlaveStep() {
        return stepBuilderFactory.get("apiSlaveStep")
            .<ProductVO, ApiRequestVO>chunk(CHUNK_SIZE)
            .reader(itemReader(null))
            .processor(itemProcessor())
            .writer(itemWriter())
            .build();
    }

    @Bean
    public Partitioner partitioner() {
        return new ProductPartitioner(dataSource);
    }

    @Bean
    @StepScope
    public ItemReader<ProductVO> itemReader(
        @Value("#{stepExecutionContext['product']}") ProductVO productVO) {

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.DESCENDING);

        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();

        queryProvider.setSelectClause("id, name, price, type");
        queryProvider.setFromClause("from product");
        queryProvider.setWhereClause("where type = :type");
        queryProvider.setSortKeys(sortKeys);

        return new JdbcPagingItemReaderBuilder<ProductVO>()
            .name("jdbc-paging-reader")
            .dataSource(dataSource)
            .pageSize(CHUNK_SIZE)
            .rowMapper(new BeanPropertyRowMapper<>(ProductVO.class))
            .parameterValues(QueryGenerator.getParameterForQuery("type", productVO.getType()))
            .queryProvider(queryProvider)
            .build();
    }

    @Bean
    public ItemProcessor<ProductVO, ApiRequestVO> itemProcessor() {

        Map<String, ItemProcessor<ProductVO, ApiRequestVO>> processorMap = new HashMap<>();

        processorMap.put("1", new ApiItemProcessor1());
        processorMap.put("2", new ApiItemProcessor2());
        processorMap.put("3", new ApiItemProcessor3());

        ProcessorClassifier<ProductVO, ItemProcessor<?, ? extends ApiRequestVO>> classifier = new ProcessorClassifier<>();

        classifier.setProcessorMap(processorMap);

        return new ClassifierCompositeItemProcessorBuilder<ProductVO, ApiRequestVO>()
            .classifier(classifier)
            .build();

    }

    @Bean
    public ItemWriter<ApiRequestVO> itemWriter() {

        Map<String, ItemWriter<ApiRequestVO>> writerMap = new HashMap<>();

        writerMap.put("1", new ApiItemWriter1(apiService1));
        writerMap.put("2", new ApiItemWriter2(apiService2));
        writerMap.put("3", new ApiItemWriter3(apiService3));

        WriterClassifier<ApiRequestVO, ItemWriter<? super ApiRequestVO>> classifier = new WriterClassifier<>();

        classifier.setWriterMap(writerMap);

        return new ClassifierCompositeItemWriterBuilder<ApiRequestVO>()
            .classifier(classifier)
            .build();
    }

}
