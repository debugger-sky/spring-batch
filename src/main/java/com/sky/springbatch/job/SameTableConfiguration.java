package com.sky.springbatch.job;

import com.sky.springbatch.entity.TbTmpUser;
import com.sky.springbatch.reader.TmpUserItemReader;
import com.sky.springbatch.repository.TbTmpUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Collections;

@Slf4j
@Configuration
public class SameTableConfiguration {

    private final int chunkSize = 2;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final TbTmpUserRepository tbTmpUserRepository;

    public SameTableConfiguration(JobBuilderFactory jobBuilderFactory,
                                  StepBuilderFactory stepBuilderFactory,
                                  TbTmpUserRepository tbTmpUserRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.tbTmpUserRepository = tbTmpUserRepository;
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
                .<TbTmpUser, TbTmpUser> chunk(chunkSize)
                .reader(ndmsDailyItemReader())
                .processor(ndmsDailyItemProcessor())
                .writer(ndmsDailyItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<TbTmpUser> ndmsDailyItemReader() {
        return new TmpUserItemReader<TbTmpUser>(tbTmpUserRepository) {{
           setRepository(tbTmpUserRepository);
           setSort(Collections.singletonMap("id", Sort.Direction.ASC));
        }};

    }

    public ItemProcessor<TbTmpUser, TbTmpUser> ndmsDailyItemProcessor() {
        return item -> {
            item.setAge(28);
            return item;
        };
    }

    public ItemWriter<TbTmpUser> ndmsDailyItemWriter() {
        return list -> tbTmpUserRepository.saveAll(list);
    }
}
