package com.example.springbatchchunk.ch03_xmlstaxeventitemreader;

import com.example.springbatchchunk.ch03_xmlstaxeventitemreader.model.Customer;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;

/**
 * XML - StaxEventItemReader
 *
 * <pre>
 *     - Java XML API
 *          - DOM(Document Object Model) 방식
 *              - 문서 전체를 메모리에 로드한 후 Tree 형태로 만들어서 데이터를 처리하는 방식, pull 방식
 *              - Element 제어는 유연하나 문서크기가 클 경우 메모리 사용이 많고 속도가 느림
 *
 *          - SAX(Simple API XML) 방식
 *              - 문서의 항목을 읽을때 마다 이벤트가 발생하여 데이터를 처리하는 Push 방식
 *              - 메모리 비용이 적고 속도가 빠른 장점이 있으나, Element 제어가 어려움
 *
 *          - StAX 방식 (Streaming API for XML)
 *              - DOM 과 SAX 의 장점과 단점을 보안한 API 모델로서 Push 와 Pull 을 동시에 제공함
 *              - XML 문서를 읽고 쓸수 있는 양방향 Parser 지원
 *              - XML 파일의 항복에서 항복으로 직접 이동하면서 Stax Parser 를 통해 구분 분석
 *              - 유형
 *                  - Iterator API 방식
 *                      - XMLEventReader 의 nextEvent() 를 호출해서 이벤트 객체를 가지고 옴
 *                      - 이벤트 객체는 XML 태그 유형 (요소, 텍스트, 주석 등) 에 대한 정보를 제공함
 *                  - Cursor API 방식
 *                      - JDBC ResultSet 처럼 작동하는 API 로서 XMLStreamReader 는 XML 문서의 다음 요소를 Cursor 를 이동한다.
 *                      - Cursor 에서 직접 메서드를 호출하여 이벤트에 대한 자세한 정보를 얻는다.
 *
 *      - Spring OXM
 *          - Spring 의 Object XML Mapping 기술로 XML 바인딩 기술을 추상화함
 *              - Marshaller
 *                  - marshall - 객체를 XML 로 직렬화하는 행위
 *              - Unmarshaller
 *                  - unmarshall - XML 를 객체로 역직렬화 하는 행위
 *              - Marshaller 와 Unmarshaller 바인딩 기능을 제공하는 오픈소스로 JaxB2, Castor, XmlBeans, Xstream 등이 있다.
 *
 *          - Spring Batch 는 특정한 XML 바인딩 기술을 강요하지 않고, Spring OXM 에 위힘한다.
 *              - 바인딩 기술을 제공하는 구현체를 선택해서 처리하도록 한다.
 *
 *      - Spring Batch XML
 *          - Spring Batch 에서는 StAX 방식으로 XML 문서를 처리하는 StaxEventItemReader 를 제공
 *          - XML 을 읽어 Java Object 에 Mapping 하고, Java Object 를 XML 로 쓸 수 있는 Transaction 구조를 지원
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
//@Configuration
public class StaxEventItemReaderConfiguration {

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

        return new StaxEventItemReaderBuilder<Customer>()
            .name("stax-item-reader")
            .resource(new ClassPathResource("/customer.xml"))
            .addFragmentRootElements("customer")
            .unmarshaller(itemUnmarshaller())
            .build();
    }

    @Bean
    public Unmarshaller itemUnmarshaller() {

        Map<String, Class<?>> aliases = new HashMap<>();

        aliases.put("customer", Customer.class);
        aliases.put("id", Long.class);
        aliases.put("firstName", String.class);
        aliases.put("lastName", String.class);

        XStreamMarshaller marshaller = new XStreamMarshaller();

        marshaller.setAliases(aliases);
        marshaller.setSupportedClasses(Customer.class);

        return marshaller;
    }
}
