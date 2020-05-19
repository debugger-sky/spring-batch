package com.sky.springbatch.job;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.separator.RecordSeparatorPolicy;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.sky.springbatch.dto.TmpUserDto;
import com.sky.springbatch.entity.TbTmpUser;
import com.sky.springbatch.repository.TbTmpUserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FileConfiguration {
	
	private final String[] names = {"name", "age", "phoneNumber"};
	
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	
	private final TbTmpUserRepository tbTmpUserRepository;
	
	private final String filePath = "C:\\Users\\user\\Desktop\\";

	public FileConfiguration(JobBuilderFactory jobBuilderFactory, 
							 StepBuilderFactory stepBuilderFactory,
							 TbTmpUserRepository tbTmpUserRepository) {
		this.jobBuilderFactory = jobBuilderFactory;
		this.stepBuilderFactory = stepBuilderFactory;
		this.tbTmpUserRepository = tbTmpUserRepository;
	}
	
	
	@Bean
	public Job fileReadJob2() throws IOException {
		return jobBuilderFactory.get("fileReadJob2")
				.start(decryptFileStep())
				.next(fileExist())
				.from(fileExist())
					.on("STOPPED") 
					.end()
				.from(fileExist())
					.on("COMPLETED")
					.to(insertDbStep())
					.end()
				.build();
	}
	
	// test
	@Bean
	public Step decryptFileStep() {		
		return stepBuilderFactory.get("decryptFileStep")
				.tasklet((contribution, chunkContext) -> {
					// 날짜 계산. 
					String yesterday = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
					String fileName = "test_" + yesterday  + ".txt.log"; //이건 아마 항상 존재하는듯.
					
					// 복호화 로직 생략
					// File file = new File(filePath + fileName);
					// 슬립 3000초 생략
					String desFileName = fileName.replace(".log", "");
					chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("desFileName", desFileName);
					// 파일 임의로 생성하는 test코드. 이미 생성되어 있는것을 가정함.
//					File newFile = new File(filePath + desFileName);
//					newFile.createNewFile();
					return RepeatStatus.FINISHED;
				})
				.build();
	}
	
	
	// test
	@Bean
	public JobExecutionDecider fileExist() {
		return (jobExecution, stepExecution) -> {
			String desFileName = jobExecution.getExecutionContext().getString("desFileName");
			log.info("this is desFileName = {}",desFileName);
			File file = new File(filePath + desFileName);		
			Boolean flag = file.exists();
			if(flag) {
				return FlowExecutionStatus.COMPLETED;
			} else {
				return FlowExecutionStatus.STOPPED;
			}
		};
	}
	
	
	@Bean
	public Step insertDbStep() throws IOException {
		return stepBuilderFactory.get("insertDbStep")
				.<TmpUserDto, TbTmpUser> chunk(4)
				.faultTolerant()
				.skip(FlatFileParseException.class)
				.skipLimit(3)
				.reader(ndmsFileReader(null))
				.processor(ndmsDailyProcessor())
				.writer(writer())
				.build();
	}
	
//	@Bean
//	@StepScope
//	public FlatFileItemReader<EmNdmsDaily> reader() throws IOException{
//
//		String desFileName = decryptFile();	//복호화 수행, 복호화한 파일명 return
//		Boolean fileExist = fileCheck(desFileName); //복호화한 파일 존재하는지 여부
//		log.info("fileExist : {}", fileExist);
//		
//		if(fileExist) {
//			//여기서 파일 읽어서 set까지.
//			FlatFileItemReader<EmNdmsDaily> reader = new FlatFileItemReader<EmNdmsDaily>();
//			reader.setEncoding(StandardCharsets.UTF_8.name());			
//			reader.setResource(new FileSystemResource(filePath + desFileName)); // 파일 리소스 셋			
//			
//			// lineMapper
//			DefaultLineMapper<EmNdmsDaily> lineMapper = new DefaultLineMapper<EmNdmsDaily>();
//			
//			DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
//			lineTokenizer.setDelimiter("	");
//			lineMapper.setLineTokenizer(lineTokenizer);
//			
//			FieldSetMapper<EmNdmsDaily> fieldSetMapper = new BeanWrapperFieldSetMapper<EmNdmsDaily>() {
//				public EmNdmsDaily mapFieldSet(FieldSet fieldSet) {
//					String name = fieldSet.readString(1);
//					Integer age = Integer.parseInt(fieldSet.readString(2));
//					String phoneNumber = fieldSet.readString(3);
//					return new EmNdmsDaily(name, age, phoneNumber);					
//				}
//			};
//			lineMapper.setFieldSetMapper(fieldSetMapper);
//			
//			reader.setLineMapper(lineMapper);
//			log.info("this is test!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//			
//			return reader;
//		} else {
//			return null;
//		}
//		
//	}
	
	@Bean
	@StepScope
	public FlatFileItemReader<TmpUserDto> ndmsFileReader(@Value("#{jobExecutionContext[desFileName]}") final String desFileName){

		return new FlatFileItemReaderBuilder<TmpUserDto>()
				.name("FileItemReader")
				.resource(new FileSystemResource(filePath + desFileName))
				.recordSeparatorPolicy(new RecordSeparatorPolicy() {					
					@Override
					public String preProcess(String record) {
						return record;
					}					
					@Override
					public String postProcess(String record) {
						return record.trim();
					}					
					@Override
					public boolean isEndOfRecord(String record) {
						return true;
					}
				})				
				.delimited().delimiter("\t")
				.names(names)
				.fieldSetMapper(new BeanWrapperFieldSetMapper<TmpUserDto>() {{
					setTargetType(TmpUserDto.class);
				}})
				.build();	
	}
	
	@Bean
	@StepScope
	public ItemProcessor<TmpUserDto, TbTmpUser> ndmsDailyProcessor() {
		log.info("ndmsDailyProcessor!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");		
		return item -> {
			TbTmpUser tbTmpUser = dtoToEntityConverter(item);
			tbTmpUser.setAge(tbTmpUser.getAge()+1);
			tbTmpUser.setCreatedDate(LocalDateTime.now());
			return tbTmpUser;
		};
	}
	
	@Bean
	@StepScope
	public ItemWriter<TbTmpUser> writer() {
		log.info("write complete??????????????????");
		return list -> tbTmpUserRepository.saveAll(list);
	}
	
	
	
	
	// 여기서부터 메서드
	
	/*
	 * 복호화 메서드
	 */
	public String decryptFile() {		
		
		// 날짜 계산. 원래는 cal 사용.
//		String yesterday = "20200402";
		String yesterday = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String fileName = "test_" + yesterday  + ".txt.log"; //이건 아마 항상 존재하는듯.
		
//		File file = new File(filePath + fileName);
		
		// 복호화 로직 생략
		// 슬립 3000초 생략
		String desFileName = fileName.replace(".log", "");
		
		// 파일 임의로 생성하는 test코드. 이미 생성되어 있는것을 가정함.
//		File newFile = new File(filePath + desFileName);
//		newFile.createNewFile();
		
		return desFileName;
	}
	
	/*
	 * file 있는지 체크
	 */
	public Boolean fileCheck(String fileName) {		
		File file = new File(filePath + fileName);		
		return file.exists();
	}
	
	/*
	 * Ndms DTO <-> Entity converter
	 */
	public TbTmpUser dtoToEntityConverter(TmpUserDto dto) {
		TbTmpUser tbTmpUser = new TbTmpUser();
		tbTmpUser.setName(dto.getName());
		tbTmpUser.setAge(Integer.parseInt(dto.getAge()));
		tbTmpUser.setPhoneNumber(dto.getPhoneNumber());
		return tbTmpUser;
	}

}
