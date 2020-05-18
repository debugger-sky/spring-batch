package com.sky.springbatch.job;

import com.sky.springbatch.dto.UserDto;
import com.sky.springbatch.entity.TbUser;
import com.sky.springbatch.repository.TbUserRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.annotation.AfterRead;
import org.springframework.batch.core.annotation.BeforeRead;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.jsr.ItemReadListenerAdapter;
import org.springframework.batch.core.listener.CompositeItemReadListener;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.listener.StepListenerSupport;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.*;

@Configuration
@Slf4j
public class FileJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final TbUserRepository tbUserRepository;

    private final String filePath = "C:\\Users\\user\\Desktop\\";
    private final String fileName = "user.txt";

    InputStream is;

    public FileJobConfiguration(JobBuilderFactory jobBuilderFactory,
                                StepBuilderFactory stepBuilderFactory,
                                TbUserRepository tbUserRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.tbUserRepository = tbUserRepository;
    }

    @Bean
    public Job fileJob() {
        return jobBuilderFactory.get("fileJob")
                .start(fileStep())
                .build();
    }

    @Bean
    public Step fileStep() {
        return stepBuilderFactory.get("fileStep")
                .<UserDto, TbUser> chunk(10)
                .reader(userItemReader())
                .faultTolerant()
                .skip(FlatFileParseException.class)
                .skipLimit(3)
                .processor(userItemProcessor())
                .writer(userItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<UserDto> userItemReader() {

        return new FlatFileItemReaderBuilder<UserDto>()
                .name("userItemReader")
                .resource(new FileSystemResource(filePath + fileName))
                .delimited().delimiter("\t")
                .names("userId", "passwd", "name")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<UserDto>() {{
                    setTargetType(UserDto.class);
                }})
                .recordSeparatorPolicy(new SimpleRecordSeparatorPolicy() {
                    @Override
                    public String postProcess(String record) {
                        return record.trim();
                    }
                })
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<UserDto, TbUser> userItemProcessor() {
        return item -> {
            TbUser user = new TbUser();
            user.setUserId(item.getUserId());
            user.setPasswd(item.getPasswd());
            user.setName(item.getName());

            return user;
        };
    }

    @Bean
    @StepScope
    public ItemWriter<TbUser> userItemWriter() {
        return tbUserRepository::saveAll;
    }

}
