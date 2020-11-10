package com.innovationchef.payjob.step1;

import com.innovationchef.entity.Pain001CSV;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;

public class Step1SynchronizedReader extends SynchronizedItemStreamReader<Pain001CSV> {

    public Step1SynchronizedReader() {
        super();
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        JobParameters parameters = stepExecution.getJobParameters();
        this.setDelegate(new Step1Reader(parameters));
    }
}
