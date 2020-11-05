package com.innovationchef.configuration;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ConcurrentHashMap;

@EnableBatchProcessing
@Import(DBConfig.class)
public class SpringBatchConfig {

    @Bean
    public ConcurrentHashMap<String, JobParameters> initJobDict() {
        return new ConcurrentHashMap<>();
    }
}
