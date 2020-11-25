package com.innovationchef.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

@Component
@EnableBatchProcessing
public class SpringBatchConfig implements BatchConfigurer {

    private JobRepository jobRepository;
    private JobLauncher jobLauncher;
    private JobExplorer jobExplorer;

    @Autowired
    private HikariDataSource dataSource;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    @Override
    public JobRepository getJobRepository() throws Exception {
        return this.jobRepository;
    }

    @Override
    public PlatformTransactionManager getTransactionManager() throws Exception {
        return this.platformTransactionManager;
    }

    @Override
    public JobLauncher getJobLauncher() throws Exception {
        return this.jobLauncher;
    }

    @Override
    public JobExplorer getJobExplorer() throws Exception {
        return this.jobExplorer;
    }

    @Bean
    public ConcurrentHashMap<String, JobParameters> initJobDict() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("processor-");
        executor.setMaxPoolSize(12);
        executor.setCorePoolSize(8);
        executor.setQueueCapacity(15);
        executor.initialize();
        return executor;
    }

    protected JobLauncher asyncJobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(this.jobRepository);
        jobLauncher.setTaskExecutor(threadPoolTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    protected JobRepository createJobRepository() throws Exception {
        JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTransactionManager(platformTransactionManager);
        factoryBean.setTablePrefix("BATCH_");
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        this.jobRepository = createJobRepository();
        JobExplorerFactoryBean factoryBean = new JobExplorerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.afterPropertiesSet();
        this.jobExplorer = factoryBean.getObject();
        this.jobLauncher = asyncJobLauncher();
    }

    private TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("jobLauncher-");
        executor.setMaxPoolSize(12);
        executor.setCorePoolSize(8);
        executor.setQueueCapacity(15);
        executor.initialize();
        return executor;
    }
}
