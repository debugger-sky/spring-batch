package com.sky.springbatch.job;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

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

import com.sky.springbatch.entity.EmNdmsDaily;
import com.sky.springbatch.entity.EmTmpNdmsDaily;
import com.sky.springbatch.repository.EmNdmsDailyRepository;
import com.sky.springbatch.repository.EmTmpNdmsDailyRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class DbConfiguration {
	
	private final int chunkSize = 5;
	
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	private final EmNdmsDailyRepository emNdmsDailyRepository;
	private final EmTmpNdmsDailyRepository emTmpNdmsDailyRepository;
	
	public DbConfiguration(JobBuilderFactory jobBuilderFactory, 
						   StepBuilderFactory stepBuilderFactory,
						   EmNdmsDailyRepository emNdmsDailyRepository,
						   EmTmpNdmsDailyRepository emTmpNdmsDailyRepository) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
		this.emNdmsDailyRepository = emNdmsDailyRepository;
		this.emTmpNdmsDailyRepository = emTmpNdmsDailyRepository;
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
				.<EmNdmsDaily, EmTmpNdmsDaily> chunk(chunkSize)
				.reader(ndmsDailyforTmpReader())
				.processor(ndmsDailyToTmpProcessor())
				.writer(ndmsDailyWriter())
				.build();
	}
	
	@Bean
	@StepScope
	public ItemReader<EmNdmsDaily> ndmsDailyforTmpReader() {
		return new RepositoryItemReaderBuilder<EmNdmsDaily>()
				.name("ndmsDailyforTmpReader")
				.repository(emNdmsDailyRepository)
				.methodName("findAll")
				.sorts(Collections.singletonMap("id", Sort.Direction.ASC))
				.pageSize(chunkSize)
				.build();
	}
	
	@Bean
	@StepScope
	public ItemProcessor<EmNdmsDaily, EmTmpNdmsDaily> ndmsDailyToTmpProcessor() {		
		return item -> {	
			emTmpNdmsDailyRepository.truncateTmpNdmsDaily();
			EmTmpNdmsDaily emTmpNdmsDaily = new EmTmpNdmsDaily();
			emTmpNdmsDaily.setName(item.getName());
			emTmpNdmsDaily.setAge(item.getAge());
			emTmpNdmsDaily.setPhoneNumber(item.getPhoneNumber());
			return emTmpNdmsDaily;
		};
	}
	
	@Bean
	@StepScope
	public ItemWriter<EmTmpNdmsDaily> ndmsDailyWriter() {
		return list -> emTmpNdmsDailyRepository.saveAll(list);
	}
	
	
}
