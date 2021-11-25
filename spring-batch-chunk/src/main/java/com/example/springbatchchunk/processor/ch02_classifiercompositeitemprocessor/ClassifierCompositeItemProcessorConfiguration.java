package com.example.springbatchchunk.processor.ch02_classifiercompositeitemprocessor;

import com.example.springbatchchunk.processor.ch02_classifiercompositeitemprocessor.custom.CustomItemProcessor1;
import com.example.springbatchchunk.processor.ch02_classifiercompositeitemprocessor.custom.CustomItemProcessor2;
import com.example.springbatchchunk.processor.ch02_classifiercompositeitemprocessor.custom.CustomItemProcessor3;
import com.example.springbatchchunk.processor.ch02_classifiercompositeitemprocessor.custom.ProcessorClassifier;
import com.example.springbatchchunk.processor.ch02_classifiercompositeitemprocessor.model.ProcessorInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.builder.ClassifierCompositeItemProcessorBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClassifierCompositeItemProcessor
 *
 * <pre>
 *     - Classifier 로 라우팅 패턴을 구현해서 ItemProcessor 구현체 중 하나를 호출하는 역할
 *
 * </pre>
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class ClassifierCompositeItemProcessorConfiguration {

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
            .<ProcessorInfo, ProcessorInfo>chunk(5)
            .reader(new ItemReader<ProcessorInfo>() {

                long cnt = 0;

                @Override
                public ProcessorInfo read()
                    throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

                    cnt++;
                    ProcessorInfo processorInfo = ProcessorInfo.builder().id(cnt).build();

                    return cnt > 3 ? null : processorInfo;
                }
            })
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

//    @Bean
//    public ItemReader<String> itemReader() {
//        List<String> items = Arrays.asList("item1", "item2", "item3", "item4", "item5");
//
//        return new ListItemReader<>(items);
//    }

    @Bean
    public ItemProcessor<ProcessorInfo, ProcessorInfo> itemProcessor() {

        // reader -> (processor -> ClassifierCompositeItemProcessor -> processor) -> writer
        //                              \   return 1 -> execute itemProcessor1   /
        //                               \  return 2 -> execute itemProcessor2  /
        //                                \ return 3 -> execute itemProcessor3 /

        ProcessorClassifier<ProcessorInfo, ItemProcessor<?, ? extends ProcessorInfo>> classifier = new ProcessorClassifier<>();

        classifier.add(1L, new CustomItemProcessor1())
            .add(2L, new CustomItemProcessor2())
            .add(3L, new CustomItemProcessor3());

        return new ClassifierCompositeItemProcessorBuilder<ProcessorInfo, ProcessorInfo>()
            .classifier(classifier) // 분류자 설정
            .build();
    }

    @Bean
    public ItemWriter<ProcessorInfo> itemWriter() {
        return items -> log.info("items={}", items);
    }
}
