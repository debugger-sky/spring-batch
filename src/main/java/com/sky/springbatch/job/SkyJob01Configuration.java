package com.sky.springbatch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@RequiredArgsConstructor // 생성사 DI를 위한 Lombok 어노테이션
@Configuration // Spring Batch의 모든 Job은 @Configuration으로 등록해서 사용
public class SkyJob01Configuration {
	
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	public SkyJob01Configuration(JobBuilderFactory jobBuilderFactory, 
								 StepBuilderFactory stepBuilderFactory) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
	}
	
	@Bean
	public Job skyJob01Job() {
		return jobBuilderFactory.get("skyJob01Job")
					.start(fileDesStep(null))
//					.next(existFileDecider(true))
//					.from(existFileDecider(true))
//						.on("EXIST")
//						.to(saveFileToDb())
//					.end()
					.build();
	}
	
	/*
	 * step1
	 * 파일  복호화
	 * 해당 파일 존재하는지까지 체크.
	 */
	@Bean
	@JobScope
	public Step fileDesStep(@Value("#{jobParameters[requestDate]}") String rqDate) {
		return stepBuilderFactory.get("fileDesStep")
				.tasklet((contribution, chunkContext) -> {
					log.warn("fileDesStep done = {}", rqDate);
					return RepeatStatus.FINISHED;
				})
				.build();
	}
	
	/*
	 * step2
	 * 위에 파일 읽어서 데이터 saveAll로 저장
	 */
	@Bean
	public Step saveFileToDb() {
		return stepBuilderFactory.get("fileDesStep")
				.tasklet((contribution, chunkContext) -> {
					log.warn("saveFileToDb done");
					return RepeatStatus.FINISHED;
				})
				.build();
	} 
	
	/*
	 * step3
	 * DB 저장 및 삭제 처리. tasklet 방식 말고 itemReader, itemProcessor, itemWriter 방식으로 진행?
	 */
//	@Bean
//	public Step saveFileToDb() {
//		return stepBuilderFactory.get("fileDesStep")
//				.tasklet((contribution, chunkContext) -> {
//					log.info("saveFileToDb done");
//					return RepeatStatus.FINISHED;
//				})
//				.build();
//	}
	
	public JobExecutionDecider existFileDecider(Boolean bool) {
		return new JobExecutionDecider() {
			
			@Override
			public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
				if(bool) {
					log.warn("existFileDecider EXIST");
					return new FlowExecutionStatus("EXIST");
				} else {
					log.warn("existFileDecider END");
					return new FlowExecutionStatus("END");
				}
				
			}
		};
	}

}
