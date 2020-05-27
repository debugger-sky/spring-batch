package com.sky.springbatch.job;

import com.sky.springbatch.dto.MovieDto;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;

@Slf4j
@Configuration
public class JsonConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public JsonConfiguration(JobBuilderFactory jobBuilderFactory,
                             StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job jsonJob() throws MalformedURLException {
        return jobBuilderFactory.get("jsonJob")
                .start(jsonStep())
                .build();
    }

    @Bean
    @JobScope
    public Step jsonStep() throws MalformedURLException {
        return stepBuilderFactory.get("jsonStep")
                .<MovieDto, String> chunk(1000)
                .reader(jsonItemReader())
                .processor(jsonItemProcessor())
                .writer(jsonItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public JsonItemReader<MovieDto> jsonItemReader() throws MalformedURLException {
        return new JsonItemReaderBuilder<MovieDto>()
                .name("jsonItemReader")
                .jsonObjectReader(new JacksonJsonObjectReader<>(MovieDto.class))
                .resource(new UrlResource("https://raw.githubusercontent.com/prust/wikipedia-movie-data/master/movies.json"))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<MovieDto, String> jsonItemProcessor() {
        return item -> {
            log.info("JSONObject ? = {}", item.getTitle());
            return "Hello";
        };
    }

    @Bean
    @StepScope
    public ItemWriter<String> jsonItemWriter() {
        return list -> {};
    }
}
