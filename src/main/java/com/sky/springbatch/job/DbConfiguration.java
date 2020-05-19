package com.sky.springbatch.job;

import java.util.Collections;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import com.sky.springbatch.entity.TbTmpUser;
import com.sky.springbatch.entity.TmpUser;
import com.sky.springbatch.repository.TbTmpUserRepository;
import com.sky.springbatch.repository.TmpUserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class DbConfiguration {
	
	private final int chunkSize = 5;
	
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	private final TbTmpUserRepository tbTmpUserRepository;
	private final TmpUserRepository tmpUserRepository;
	
	public DbConfiguration(JobBuilderFactory jobBuilderFactory, 
						   StepBuilderFactory stepBuilderFactory,
						   TbTmpUserRepository tbTmpUserRepository,
						   TmpUserRepository tmpUserRepository) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
		this.tbTmpUserRepository = tbTmpUserRepository;
		this.tmpUserRepository = tmpUserRepository;
	}
	
	
	@Bean
	public Job tmpJob() {
		return jobBuilderFactory.get("tmpJob")
				.start(tmpStep())
				.build();
	}
	
	@Bean
	@JobScope
	public Step tmpStep() {
		return stepBuilderFactory.get("tmpStep")
				.<TbTmpUser, TmpUser> chunk(chunkSize)
				.reader(ndmsDailyforTmpReader())
				.processor(ndmsDailyToTmpProcessor())
				.writer(ndmsDailyWriter())
				.build();
	}
	
	@Bean
	@StepScope
	public ItemReader<TbTmpUser> ndmsDailyforTmpReader() {
		return new RepositoryItemReaderBuilder<TbTmpUser>()
				.name("ndmsDailyforTmpReader")
				.repository(tbTmpUserRepository)
				.methodName("findAll")
				.sorts(Collections.singletonMap("id", Sort.Direction.ASC))
				.pageSize(chunkSize)
				.build();
	}
	
	@Bean
	@StepScope
	public ItemProcessor<TbTmpUser, TmpUser> ndmsDailyToTmpProcessor() {
		return item -> {	
			tmpUserRepository.truncateTmpNdmsDaily();
			TmpUser tmpUser = new TmpUser();
			tmpUser.setName(item.getName());
			tmpUser.setAge(item.getAge());
			tmpUser.setPhoneNumber(item.getPhoneNumber());
			return tmpUser;
		};
	}
	
	@Bean
	@StepScope
	public ItemWriter<TmpUser> ndmsDailyWriter() {
		return list -> tmpUserRepository.saveAll(list);
	}
	
	
}
