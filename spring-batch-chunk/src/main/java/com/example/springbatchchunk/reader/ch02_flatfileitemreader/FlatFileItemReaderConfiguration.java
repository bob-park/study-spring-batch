package com.example.springbatchchunk.reader.ch02_flatfileitemreader;

import com.example.springbatchchunk.reader.ch02_flatfileitemreader.flatfile.CustomerFieldSetMapper;
import com.example.springbatchchunk.reader.ch02_flatfileitemreader.flatfile.DefaultLineMapper;
import com.example.springbatchchunk.reader.ch02_flatfileitemreader.model.Customer;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

/**
 * FlatFileItemReader
 *
 * <pre>
 *     - 2차원 데이터(표) 로 표현된 유형의 파일을 처리하는 ItemReader
 *     - 일반적으로 고정 위치로 정의된 데이터 필드나 특수 문자에 의해 구별된 데이터의 행을 읽는다.
 *     - Resource 와 LineMapper 두가지 요소가 필요하다.
 *
 *     - Resource
 *          - FileSystemResource
 *          - ClassPathResource
 *
 *     - LineMapper
 *          - 파일의 라인 한줄을 Object 로 변환해서 FlatFileItemReader 로 리턴한다.
 *          - 단순히 문자열을 받기 때문에 문자열을 토큰화해서 객체로 매핑하는 과정이 필요하다.
 *          - LineTokenizer 와 FieldSetMapper 를 사옹해서 처리한다.
 *
 *          - FieldSet
 *              - 라인을 필드로 구분해서 만든 배열 토큰을 전달하면 토큰 필드를 참조할 수 있도록 한다.
 *              - JDBC 의 ResultSet 과 유사하다.
 *
 *          - LineTokenizer
 *              - 입력받은 라인을 FieldSet 으로 변환해서 리턴한다.
 *              - 파일마다 형식이 다르기 때문에 문자열을 FieldSet 으로 변환하는 작업을 추상화시켜야한다.
 *
 *          - FieldSetMapper
 *              - FieldSet 객체를 받아서 원하는 객체로 매핑해서 리턴한다.
 *              - JdbcTemplate 의 RowMapper 와 동일한 패턴을 사용한다.
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class FlatFileItemReaderConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job batchJob1() {
        return jobBuilderFactory.get("batchJob1")
            .incrementer(new RunIdIncrementer())
            .start(step1())
            .next(step2())
            .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .<Customer, Customer>chunk(5)
            .reader(itemReader())
            .writer(items -> log.info("items={}", items))
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
    public ItemReader<Customer> itemReader() {

        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        lineMapper.setTokenizer(new DelimitedLineTokenizer());
        lineMapper.setFieldSetMapper(new CustomerFieldSetMapper());

        return new FlatFileItemReaderBuilder<Customer>()
            .name("customer") // 이름 설정, ExecutionContext 내에서 구분하기 위한 key 로 저장
            .resource(new ClassPathResource("/customer.csv")) // 읽어야할 리소스 설정
//            .delimited().delimiter("|") // 파일 구분자를 기준으로 파일을 읽어들이는 설정
//            .fixedLength() // 파일의 고정길이를 기준으로 파일을 읽어들이는 설정
//            .addColumns(Range...) // 고정길이 범위를 정하는 설정
//            .names(String[]) // LineTokenizer 로 구분된 라인의 항목을 객체의 필드명과 맵핑하도록 설정
//            .targetType(Class) // 라인의 각 항목과 매핍할 객체 타입 설정
//            .addComment(String) // 무시할 라인의 코멘트 기호 설정
//            .strict(boolean) // 라인을 읽거나 토큰화할때 Parsing 예외가 발생하지 않도록 검증 생략하도록 설정
            .encoding(StandardCharsets.UTF_8.name()) // 파일 인코딩 설정
            .linesToSkip(1) // 파일 상단에 있는 무시할 라인 수 설정
//            .saveState(boolean) // 상태정보를 저장할 것인지 설정
            .lineMapper(lineMapper) // LineMapper 설정
//            .fieldSetMapper(FieldSetMapper) // FieldSetMapper 설정
//            .lineTokenizer(LineTokenizer) // LineTokenizer 설정
            .build();
    }


}
