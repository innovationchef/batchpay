package com.innovationchef.job;

import com.innovationchef.configuration.BatchJobRegistrar;
import com.innovationchef.configuration.SpringBatchConfig;
import com.innovationchef.constant.BatchConstant;
import com.innovationchef.entity.Pain001CSV;
import com.innovationchef.job.step1.ItemCountListener;
import com.innovationchef.job.step1.Step1Processor;
import com.innovationchef.job.step1.Step1Reader;
import com.innovationchef.job.step1.Step1Writer;
import com.innovationchef.service.PaymentApiCall;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Import(SpringBatchConfig.class)
public class JobConfig {

    private JobBuilder jobBuilder;
    private StepBuilder stepBuilder;
    private SessionFactory sessionFactory;
    private BatchJobRegistrar jobRegistrar;

    private PaymentApiCall paymentApiCall;

    public JobConfig(PaymentApiCall paymentApiCall,
                     BatchJobRegistrar jobRegistrar,
                     JobRepository jobRepository,
                     SessionFactory sessionFactory,
                     PlatformTransactionManager txnMgr) {
        this.paymentApiCall = paymentApiCall;
        this.sessionFactory = sessionFactory;
        this.jobRegistrar = jobRegistrar;
        this.jobBuilder = new JobBuilder(BatchConstant.JOB_NAME).repository(jobRepository);
        this.stepBuilder = new StepBuilder(BatchConstant.STEP_NAME).repository(jobRepository).transactionManager(txnMgr);
    }

    @Bean
    public Job jobBuilder() throws DuplicateJobException {
        Job job = this.jobBuilder
                .incrementer(new RunIdIncrementer())
                .start(processPaymentStep())
                .build();
        this.jobRegistrar.registerJob(job);
        return job;
    }

    private Step processPaymentStep() {
        return this.stepBuilder.<Pain001CSV, Pain001CSV>chunk(10)
                .reader(new Step1Reader())
                .processor(new Step1Processor(this.paymentApiCall))
                .writer(new Step1Writer(this.sessionFactory))
                .listener(new ItemCountListener())
                .build();
    }
}
