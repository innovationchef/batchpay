package com.innovationchef.payjob;

import com.innovationchef.batchcommons.FilePathParamValidator;
import com.innovationchef.batchcommons.ItemCountListener;
import com.innovationchef.configuration.BatchJobRegistrar;
import com.innovationchef.configuration.SpringBatchConfig;
import com.innovationchef.constant.BatchConstant;
import com.innovationchef.entity.Pain001CSV;
import com.innovationchef.payjob.step1.Step1Processor;
import com.innovationchef.payjob.step1.Step1SynchronizedReader;
import com.innovationchef.payjob.step1.Step1Writer;
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
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Import(SpringBatchConfig.class)
public class PayJobConfig {

    private JobBuilder jobBuilder;
    private StepBuilder stepBuilder;
    private SessionFactory sessionFactory;
    private BatchJobRegistrar jobRegistrar;
    private TaskExecutor taskExecutor;

    private PaymentApiCall paymentApiCall;

    public PayJobConfig(PaymentApiCall paymentApiCall,
                        TaskExecutor taskExecutor,
                        BatchJobRegistrar jobRegistrar,
                        JobRepository jobRepository,
                        SessionFactory sessionFactory,
                        PlatformTransactionManager txnMgr) {
        this.paymentApiCall = paymentApiCall;
        this.sessionFactory = sessionFactory;
        this.taskExecutor = taskExecutor;
        this.jobRegistrar = jobRegistrar;
        this.jobBuilder = new JobBuilder(BatchConstant.PAY_JOB_NAME).repository(jobRepository);
        this.stepBuilder = new StepBuilder(BatchConstant.PAY_STEP_NAME).repository(jobRepository).transactionManager(txnMgr);
    }

    @Bean("payJob")
    public Job jobBuilder() throws DuplicateJobException, Exception {
        Job job = this.jobBuilder
                .incrementer(new RunIdIncrementer())
                .start(processPaymentStep())
                .validator(new FilePathParamValidator())
                .build();
        this.jobRegistrar.registerJob(job);
        return job;
    }

    private Step processPaymentStep() throws Exception {
        return this.stepBuilder.<Pain001CSV, Pain001CSV>chunk(10)
                .reader(new Step1SynchronizedReader())
                .processor(new Step1Processor(this.paymentApiCall))
                .writer(new Step1Writer(this.sessionFactory))
                .listener(new ItemCountListener())
                .taskExecutor(this.taskExecutor)
                .throttleLimit(10)
                .build();
    }
}
