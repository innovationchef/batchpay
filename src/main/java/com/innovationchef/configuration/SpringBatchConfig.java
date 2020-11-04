package com.innovationchef.configuration;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Import;

@EnableBatchProcessing
@Import(DBConfig.class)
public class SpringBatchConfig {

}
