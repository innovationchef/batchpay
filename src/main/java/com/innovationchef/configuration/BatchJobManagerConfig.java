package com.innovationchef.configuration;

import com.innovationchef.batchcommons.JobManager;
import com.innovationchef.constant.BatchConstant;
import com.innovationchef.custjob.CustJobConfig;
import com.innovationchef.payjob.PayJobConfig;
import com.innovationchef.service.PaymentService;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchJobManagerConfig {

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

    @Bean
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
        CustJobConfig jobConfig = new CustJobConfig(sessionFactory, jobRepository, txnMgr);
        return jobConfig.jobBuilder();
    }

    @Bean(BatchConstant.PAY_JOB_NAME)
    public Job payJob(PaymentService paymentService,
                       TaskExecutor taskExecutor,
                       SessionFactory sessionFactory,
                       JobRepository jobRepository,
                       PlatformTransactionManager txnMgr) throws DuplicateJobException {
        PayJobConfig jobConfig = new PayJobConfig(paymentService, taskExecutor, sessionFactory, jobRepository, txnMgr);
        return jobConfig.jobBuilder();
    }
}
