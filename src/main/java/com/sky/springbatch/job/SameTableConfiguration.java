package com.sky.springbatch.job;

import com.sky.springbatch.entity.EmNdmsDaily;
import com.sky.springbatch.entity.EmTmpNdmsDaily;
import com.sky.springbatch.reader.NdmsDailyItemReader;
import com.sky.springbatch.repository.EmNdmsDailyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.AbstractMethodInvokingDelegator;
import org.springframework.batch.item.adapter.DynamicMethodInvocationException;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.MethodInvoker;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class SameTableConfiguration {

    private final int chunkSize = 2;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EmNdmsDailyRepository emNdmsDailyRepository;

    public SameTableConfiguration(JobBuilderFactory jobBuilderFactory,
                                  StepBuilderFactory stepBuilderFactory,
                                  EmNdmsDailyRepository emNdmsDailyRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.emNdmsDailyRepository = emNdmsDailyRepository;
    }

    @Bean
    public Job sameTableJob() {
        return jobBuilderFactory.get("sameTableJob")
                .start(sameTableStep())
                .build();
    }

    @Bean
    @JobScope
    public Step sameTableStep() {
        return stepBuilderFactory.get("sameTableStep")
                .<EmNdmsDaily, EmNdmsDaily> chunk(chunkSize)
                .reader(ndmsDailyItemReader())
                .processor(ndmsDailyItemProcessor())
                .writer(ndmsDailyItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<EmNdmsDaily> ndmsDailyItemReader() {
//        List<Integer> arg = new ArrayList<Integer>();
//        arg.add(26);
//
//        return new RepositoryItemReaderBuilder<EmNdmsDaily>()
//                .name("ndmsDailyItemReader")
//                .repository(emNdmsDailyRepository)
//                .methodName("findByAge")
//                .arguments(arg)
//                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
//                .pageSize(chunkSize)
//                .build();

        return new NdmsDailyItemReader<EmNdmsDaily>(emNdmsDailyRepository) {{
           setRepository(emNdmsDailyRepository);
           setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        }};

    }

    public ItemProcessor<EmNdmsDaily, EmNdmsDaily> ndmsDailyItemProcessor() {
        return item -> {
            item.setAge(28);
            return item;
        };
    }

    public ItemWriter<EmNdmsDaily> ndmsDailyItemWriter() {
        return list -> emNdmsDailyRepository.saveAll(list);
    }



    private Object doInvoke(MethodInvoker invoker) throws Exception{
        try {
            invoker.prepare();
        }
        catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new DynamicMethodInvocationException(e);
        }

        try {
            return invoker.invoke();
        }
        catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception) {
                throw (Exception) e.getCause();
            }
            else {
                throw new AbstractMethodInvokingDelegator.InvocationTargetThrowableWrapper(e.getCause());
            }
        }
        catch (IllegalAccessException e) {
            throw new DynamicMethodInvocationException(e);
        }
    }
}
