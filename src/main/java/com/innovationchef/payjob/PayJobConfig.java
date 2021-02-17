package com.innovationchef.payjob;

import com.innovationchef.batchcommons.FilePathParamValidator;
import com.innovationchef.batchcommons.ItemCountListener;
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
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

public class PayJobConfig {

    private SessionFactory sessionFactory;
    private TaskExecutor taskExecutor;
    private JobRepository jobRepository;
    private PaymentApiCall paymentApiCall;
    private PlatformTransactionManager txnMgr;

    public PayJobConfig(PaymentApiCall paymentApiCall,
                        TaskExecutor taskExecutor,
                        SessionFactory sessionFactory,
                        JobRepository jobRepository,
                        PlatformTransactionManager txnMgr) {
        this.paymentApiCall = paymentApiCall;
        this.sessionFactory = sessionFactory;
        this.taskExecutor = taskExecutor;
        this.jobRepository = jobRepository;
        this.txnMgr = txnMgr;
    }

    public Job jobBuilder() throws DuplicateJobException {
        return new JobBuilder(BatchConstant.PAY_JOB_NAME)
                .repository(this.jobRepository)
                .validator(new FilePathParamValidator())
                .incrementer(new RunIdIncrementer())
                .start(processPaymentStep())
                .build();
    }

    private Step processPaymentStep() {
        return new StepBuilder(BatchConstant.PAY_STEP_NAME)
                .repository(this.jobRepository)
                .transactionManager(this.txnMgr)
                .<Pain001CSV, Pain001CSV>chunk(10)
                .reader(new Step1SynchronizedReader())
                .processor(new Step1Processor(this.paymentApiCall))
                .writer(new Step1Writer(this.sessionFactory))
                .listener(new ItemCountListener())
                .taskExecutor(this.taskExecutor)
                .throttleLimit(10)
                .allowStartIfComplete(false)
                .build();
    }
}
