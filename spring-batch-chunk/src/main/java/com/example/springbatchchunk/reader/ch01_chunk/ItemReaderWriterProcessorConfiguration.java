package com.example.springbatchchunk.reader.ch01_chunk;

import com.example.springbatchchunk.reader.ch01_chunk.model.Customer;
import com.example.springbatchchunk.reader.ch01_chunk.processor.CustomItemProcessor;
import com.example.springbatchchunk.reader.ch01_chunk.reader.CustomItemReaderV1;
import com.example.springbatchchunk.reader.ch01_chunk.writer.CustomItemWriterV1;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;

/**
 * ItemReader / ItemWriter / ItemProcessor
 *
 * <pre>
 *     - ItemReader
 *          - 다양한 입력으로부터 Data 를 읽어서 제공하는 Interface
 *              - Flat File - csv, txt
 *              - XML, Json
 *              - Database
 *              - JMS, RabbitMQ 와 같은 Message Queuing 서비스
 *              - Custom Reader - 구현 시 Multi-Thread 환경에서 Thread 에 안전하게 구현할 필요가 있음
 *          - ChunkOrientedTasklet 실행 시 필수적 요소로 설정해야한다.
 *
 *          - T read()
 *              - 입력 데이터를 일고 다음 데이터로 이동한다.
 *              - 아이템 하나를 리턴하며, 더이상 아이템이 없는 경우 null 리턴
 *              - 아이템 하나는 파일의 한줄, DB 의 한 row 혹은 XML 파일에서 하나의 Element 가 될 수 있다.
 *              - 더이상 처리해야할 Item 이 없어도 예외가 발생하지 않고, ItemProcessor 와 같은 다음 단계로 넘어간다.
 *
 *
 *          - 다수의 구현체들이 ItemReader 와 ItemStream interface 를 동시에 구현하고 있음
 *              - File 의 Stream 을 열거나 종료, DB Connection 을 열거나 종료, 입력 장치 초기화 등의 작업
 *              - ExecutionContext 에 Read 와 관련된 여러가지 상태 정보를 저장해서 재시작 시 다시 참조 하도록 지원
 *
 *
 *     - ItemWriter
 *          - Chunk 단위로 Data 를 받아 일괄 출력 작업을 위한 Interface
 *              - Flat File - csv, txt
 *              - XML, Json
 *              - Database
 *              - JMS, RabbitMQ 와 같은 Message Queuing 서비스
 *              - Mail Service
 *              - Custom Writer
 *          - Item 하나가 아닌 Item List 를 전달받는다.
 *          - ChunkOrientedTasklet 실행 시 필수적 요소로 설정해야한다.
 *
 *          - void Writer(List<? extends T> items)
 *              - 출력 데이터를 아이템 리스트로 받아 처리
 *              - 출력이 완료되고 Transaction 이 종료되면, 새로운 Chunk 단위 Process 로 이동
 *
 *          - 다수의 구현체들이 ItemWriter 와 ItemStream 을 동시에 구현하고 있다.
 *              - File 의 Stream 을 열거나 종료, DB Connection 을 열거나 종료, 출력 장치 초기화 등의 작업
 *
 *          - 보통 ItemReader 구현체와 1:1 대응 관계인 구현체들로 구성되어 있다.
 *
 *
 *
 *     - ItemProcessor
 *          - 데이터를 출력하기 전에 Data 를 가공 - 변형 - 필터링 역할
 *          - ItemReader 및 ItemWriter 와 분리되어 비지니스 로직을 구현할 수 있다.
 *          - ItemReader 로 부터 받은 아이템을 특정 Type 으로 변환 후 ItemWriter 에 넘겨줄 수 있다.
 *          - ItemReader 로 부터 받은 아이템들 중 필터과정을 거쳐 원하는 아이템들만 ItemWriter 에게 넘겨줄 수 있다.
 *              - ItemProcessor 에서 process() 실행 결과 null 을 반환하면 Chunk<O> 에 저장되기 않기 때문에 결국 ItemWriter 에 전달되지 않는다.
 *          - ChunkOrientedTasklet 실행 시 선택적 요소이기 때문에 청크 기반 프로세싱에서 ItemProcessor 단계가 반드시 필요한 것은 아니다.
 *
 *          - O process(I item)
 *              - <I> generic 은 ItemReader 에서 받을 데이터 타입 지정
 *              - <O> generic 은 ItemWriter 에게 보낼 데이터 타입 지정
 *              - 아이템 하나씩 가공처리하며 null 리턴할 경우 해당 아이템은 Chunk<O> 에 저장되지 않는다.
 *
 *          - 구현체
 *              - ItemStream 을 구현하지 않는다.
 *              - 거의 대부분 Customizing 해서 사용하기 때문에 기본적으로 제공되는 구현체가 적다.
 *
 *
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class ItemReaderWriterProcessorConfiguration {

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
    @JobScope
    public Step step1() {
        return stepBuilderFactory.get("step1")
            .<Customer, Customer>chunk(3)
            .reader(itemReader())
            .processor(itemProcessor())
            .writer(itemWriter())
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
        return new CustomItemReaderV1(Arrays.asList(
            new Customer("user1"),
            new Customer("user2"),
            new Customer("user3")
        ));
    }

    @Bean
    public ItemProcessor<Customer, Customer> itemProcessor() {
        return new CustomItemProcessor();
    }

    @Bean
    public ItemWriter<Customer> itemWriter() {
        return new CustomItemWriterV1();
    }


}
