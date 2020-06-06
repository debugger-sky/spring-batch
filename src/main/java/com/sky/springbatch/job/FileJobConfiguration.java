package com.sky.springbatch.job;

import com.sky.springbatch.dto.UserDto;
import com.sky.springbatch.entity.TbUser;
import com.sky.springbatch.repository.TbUserRepository;
import com.sky.springbatch.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.*;
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
    private final CommonUtil commonUtil;

    private final String filePath = "C:\\Users\\user\\Desktop\\";
    private final String fileName = "user.txt";

    InputStream is;

    public FileJobConfiguration(JobBuilderFactory jobBuilderFactory,
                                StepBuilderFactory stepBuilderFactory,
                                TbUserRepository tbUserRepository,
                                CommonUtil commonUtil) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.tbUserRepository = tbUserRepository;
        this.commonUtil = commonUtil;
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
                .listener(new SkipListenerSupport<UserDto, TbUser>(){
                    @Override
                    public void onSkipInRead(Throwable t) {
                        log.error("exception message = {}", t.getMessage());
                        log.error("exception cause={}", t.getCause().getMessage());
                    }
                })
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
                .names(commonUtil.declaredFieldsName(UserDto.class))
                .targetType(UserDto.class)
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
            user.setIntValue(item.getIntValue());

            return user;
        };
    }

    @Bean
    @StepScope
    public ItemWriter<TbUser> userItemWriter() {
        return tbUserRepository::saveAll;
    }

}
