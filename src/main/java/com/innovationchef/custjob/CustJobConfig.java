package com.innovationchef.custjob;

import com.innovationchef.batchcommons.FilePathParamValidator;
import com.innovationchef.batchcommons.ItemCountListener;
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
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

public class CustJobConfig {

    private SessionFactory sessionFactory;
    private TaskExecutor taskExecutor;
    private JobRepository jobRepository;
    private PlatformTransactionManager txnMgr;

    public CustJobConfig(SessionFactory sessionFactory,
                         TaskExecutor taskExecutor,
                         JobRepository jobRepository,
                         PlatformTransactionManager txnMgr) {
        this.sessionFactory = sessionFactory;
        this.taskExecutor = taskExecutor;
        this.jobRepository = jobRepository;
        this.txnMgr = txnMgr;
    }

    public Job jobBuilder() throws DuplicateJobException {
        return new JobBuilder(BatchConstant.CUST_JOB_NAME)
                .repository(this.jobRepository)
                .incrementer(new RunIdIncrementer())
                .validator(new FilePathParamValidator())
                .start(saveInputStep())
                .build();
    }

    private Step saveInputStep() {
        return new StepBuilder(BatchConstant.CUST_STEP_NAME)
                .repository(this.jobRepository)
                .transactionManager(this.txnMgr)
                .<CustomerCSV, CustomerCSV>chunk(10)
                .reader(new Step1Reader())
                .processor(new Step1Processor())
                .writer(new Step1Writer(this.sessionFactory))
                .listener(new ItemCountListener())
                .build();
    }
}