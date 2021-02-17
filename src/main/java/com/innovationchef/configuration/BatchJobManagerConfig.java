package com.innovationchef.configuration;

import com.innovationchef.batchcommons.JobManager;
import com.innovationchef.constant.BatchConstant;
import com.innovationchef.custjob.CustJobConfig;
import com.innovationchef.payjob.PayJobConfig;
import com.innovationchef.service.PaymentApiCall;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchJobManagerConfig {

    @Bean
    @ConditionalOnMissingBean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("processor-");
        executor.setMaxPoolSize(12);
        executor.setCorePoolSize(8);
        executor.setQueueCapacity(15);
        executor.initialize();
        return executor;
    }

    @Bean
    @ConditionalOnMissingBean
    public JobManager jobManager(JobExplorer jobExplorer,
                                 JobOperator jobOperator,
                                 JobRegistry jobRegistry,
                                 JobLauncher jobLauncher) {
        return new JobManager(jobExplorer, jobOperator, jobRegistry, jobLauncher);
    }

    @Bean(BatchConstant.CUST_JOB_NAME)
    public Job custJob(SessionFactory sessionFactory,
                       TaskExecutor taskExecutor,
                       JobRepository jobRepository,
                       PlatformTransactionManager txnMgr) throws DuplicateJobException {
        CustJobConfig jobConfig = new CustJobConfig(sessionFactory, taskExecutor, jobRepository, txnMgr);
        return jobConfig.jobBuilder();
    }

    @Bean(BatchConstant.PAY_JOB_NAME)
    public Job custJob(PaymentApiCall paymentApiCall,
                       TaskExecutor taskExecutor,
                       SessionFactory sessionFactory,
                       JobRepository jobRepository,
                       PlatformTransactionManager txnMgr) throws DuplicateJobException {
        PayJobConfig jobConfig = new PayJobConfig(paymentApiCall, taskExecutor, sessionFactory, jobRepository, txnMgr);
        return jobConfig.jobBuilder();
    }
}
