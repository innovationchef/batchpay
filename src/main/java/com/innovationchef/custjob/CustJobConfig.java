package com.innovationchef.custjob;

import com.innovationchef.batchcommons.ItemCountListener;
import com.innovationchef.configuration.BatchJobRegistrar;
import com.innovationchef.configuration.SpringBatchConfig;
import com.innovationchef.constant.BatchConstant;
import com.innovationchef.custjob.step1.Step1Processor;
import com.innovationchef.custjob.step1.Step1Reader;
import com.innovationchef.custjob.step1.Step1Writer;
import com.innovationchef.entity.CustomerCSV;
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
public class CustJobConfig {

    private JobBuilder jobBuilder;
    private StepBuilder stepBuilder;
    private SessionFactory sessionFactory;
    private BatchJobRegistrar jobRegistrar;

    public CustJobConfig(BatchJobRegistrar jobRegistrar,
                         JobRepository jobRepository,
                         SessionFactory sessionFactory,
                         PlatformTransactionManager txnMgr) {
        this.sessionFactory = sessionFactory;
        this.jobRegistrar = jobRegistrar;
        this.jobBuilder = new JobBuilder(BatchConstant.CUST_JOB_NAME).repository(jobRepository);
        this.stepBuilder = new StepBuilder(BatchConstant.CUST_STEP_NAME).repository(jobRepository).transactionManager(txnMgr);
    }

    @Bean("custJob")
    public Job jobBuilder() throws DuplicateJobException {
        Job job = this.jobBuilder
                .incrementer(new RunIdIncrementer())
                .start(saveInputStep())
                .build();
        this.jobRegistrar.registerJob(job);
        return job;
    }

    private Step saveInputStep() {
        return this.stepBuilder.<CustomerCSV, CustomerCSV>chunk(10)
                .reader(new Step1Reader())
                .processor(new Step1Processor())
                .writer(new Step1Writer(this.sessionFactory))
                .listener(new ItemCountListener())
                .build();
    }
}