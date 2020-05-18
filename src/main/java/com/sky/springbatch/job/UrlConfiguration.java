package com.sky.springbatch.job;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.sky.springbatch.dto.UrlDto;
import com.sky.springbatch.repository.EmNdmsDailyRepository;
import com.sky.springbatch.repository.EmTmpNdmsDailyRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class UrlConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	
	public UrlConfiguration(JobBuilderFactory jobBuilderFactory, 
						    StepBuilderFactory stepBuilderFactory,
						    EmNdmsDailyRepository emNdmsDailyRepository,
						    EmTmpNdmsDailyRepository emTmpNdmsDailyRepository) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}
	
//	@Bean
//	public Job urlJob() {
//		return jobBuilderFactory.get("urlJob")
//				.start(tmpStep())
//				.build();
//	}
	
	@Bean
	@StepScope
	public ItemReader<UrlDto> urlItemReader() {
		return new FlatFileItemReaderBuilder<UrlDto>()
				.name("urlItemReader")
				.resource(new FileSystemResource("https://goddaehee.tistory.com/161"))
				.build();
	}
	
	
}
