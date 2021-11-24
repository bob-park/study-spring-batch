package com.example.springbatchchunk.writer.ch01_flatfileitemwriter;

import com.example.springbatchchunk.writer.model.CustomerV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;

/**
 * FlatFileItemWriter
 *
 * <pre>
 *     - 2차원 데이터(표)로 표현된 유형의 파일을 처리하는 ItemWriter
 *     - 고정 위치로 정의된 데이터 필드나 특수 문자에 의해 구별된 데이터의 행을 기록한다.
 *     - Resource 와 LineAggregator 두가지가 요소가 필요하다.
 *
 *     - LineAggregator
 *          - Item 을 받아서 String 으로 변환하여 리턴한다.
 *          - FieldExtractor 를 사용해서 처리할 수 있다.
 *          - 구현체
 *              - PassThroughLineAggregator
 *                  - 전달된 아이템을 단순히 문자열로 변환
 *              - DelimitedLineAggregator
 *                  - 전달된 배열을 구분자로 구분하여 문자열로 합침
 *              - FormatterLineAggregator
 *                  - 전달된 배열을 고정길이로 구분하여 문자열로 합침
 *
 *
 *     - FieldExtractor
 *          - 전달 받은 Item 객체의 필드를 배열로 만들고, 배열을 합쳐서 문자열을 만들도록 구현하도록 제공하는 인터페이스
 *          - 구현체
 *              - BeanWrapperFieldExtractor
 *                  - 전달된 객체의 필드들을 배열로 반환
 *              - PassThroughFieldExtractor
 *                  - 전달된 Collection 을 배열로 반환
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class FlatFileItemWriterConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .start(step1())
            .next(step2())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .tasklet((contribution, chunkContext) -> {
                log.info("step1 has executed.");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
            .tasklet((contribution, chunkContext) -> {
                log.info("step2 has executed.");
                return RepeatStatus.FINISHED;
            })
            .build();
    }

    @Bean
    public ItemWriter<CustomerV1> itemWriter(){
        return new FlatFileItemWriterBuilder<CustomerV1>()
            .name("flat-file-writer")
//            .resource(resource) // 쓰기할 리소스 설정
//            .lineAggregator(lineAggregator) // 객체를 String 으로 변환하는 LineAggregator 객체 설정
//            .append(true) // 존재하는 파일에 내용을 추가할 것인지 여부 설정
//            .fieldExtractor(FieldExtractor<T>) // 객체 필드를 추출해서 배열로 만드는 FieldExtractor 설정
//            .headerCallback(FlatFileHeaderCallback) // 헤더를 파일에 쓰기 위한 callback
//            .footerCallback(FlatFileFooterCallback) // 풋터를 파일에 쓰기 위한 callback
//            .shouldDeleteIfExists(true) // 파일이 이미 존재한다면 삭제
//            .shouldDeleteIfExists(true) // 파일의 내용이 비어 있다면 삭제
//            .delimited().delimiter(",") // 파일의 구분자를 기준으로 파일을 작성하도록 설정
//            .formatted().format("") // 파일의 고정길이를 기준으로 파일을 작성하도록 설정
            .build();
    }

}
