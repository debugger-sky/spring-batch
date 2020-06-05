package com.sky.springbatch.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;

@Configuration
@Slf4j
public class ErrorJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    public ErrorJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job errorJob() {
        return jobBuilderFactory.get("errorJob")
                .start(errorStep())
                .build();
    }

    @Bean
    @JobScope
    public TaskletStep errorStep() {
        return stepBuilderFactory.get("errorStep")
                .tasklet((contribution, chunkContext) -> {
                    throw new EmptyResultDataAccessException(1); //org.springframework.dao.EmptyResultDataAccessException
                })
                .build();
    }
}
