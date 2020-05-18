package com.sky.springbatch.job;

import com.sky.springbatch.entity.TbUser;
import com.sky.springbatch.repository.TbUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class ListJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final TbUserRepository tbUserRepository;

    public ListJobConfiguration(JobBuilderFactory jobBuilderFactory,
                                StepBuilderFactory stepBuilderFactory,
                                TbUserRepository tbUserRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.tbUserRepository = tbUserRepository;
    }

    @Bean
    public Job listJob() {
        return jobBuilderFactory.get("listJob")
                .start(listStep())
                .build();
    }

    @Bean
    public Step listStep() {
        return stepBuilderFactory.get("listStep")
                .<TbUser, TbUser> chunk(3)
                .reader(listItemReader())
                .processor(listItemProcessor())
                .writer(listItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<TbUser> listItemReader() {
        List<TbUser> list = tbUserRepository.findByName("1하늘");
        return new ListItemReader<>(list);
    }

    @Bean
    @StepScope
    public ItemProcessor<TbUser, TbUser> listItemProcessor() {
        return item -> {
            item.setPasswd("thisIsTest202005");
            log.info("순서!");
            return item;
        };
    }

    @Bean
    @StepScope
    public ItemWriter<TbUser> listItemWriter() {
        return tbUserRepository::saveAll;
    }

    public void updateDb() {

        List<TbUser> list = tbUserRepository.findByName("1하늘");
        if(list != null && list.size() > 0) {
            List<TbUser> queue = new ArrayList<>();

            for(TbUser user : list) {
                user.setPasswd("thisIsTest");
                queue.add(user);

                if(queue.size() == 3) {
                    tbUserRepository.saveAll(queue);
                    queue.clear();
                }
            }

            if(queue.size() > 0) {
                tbUserRepository.saveAll(queue);
            }

        }

    }
}
