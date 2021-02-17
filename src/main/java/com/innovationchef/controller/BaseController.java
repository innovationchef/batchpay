package com.innovationchef.controller;

import com.innovationchef.batchcommons.JobManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.DuplicateJobException;

import java.util.Optional;

@Log4j2
public abstract class BaseController {

    private final Job job;
    private final JobManager jobManager;

    public BaseController(Job job, JobManager jobManager) throws DuplicateJobException {
        this.job = job;
        this.jobManager = jobManager;
        this.jobManager.registerJob(job);
    }
    
    void runJob(JobParameters parameters) {
        Optional<JobExecution> execution = this.jobManager.runJob(this.job, parameters);
        if (execution.isPresent()) {
            log.info("Started job: {} with executionId: {}", this.job.getName(), execution.get().getId());
        } else {
            log.info("Requested job: {} did not start", this.job.getName());
        }
    }
}
