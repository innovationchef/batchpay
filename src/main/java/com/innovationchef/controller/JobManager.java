package com.innovationchef.controller;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Component
public class JobManager implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    private JobRegistry jobRegistry;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private ConcurrentHashMap<String, JobParameters> jobsMap;

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        Set<String> jobNames = this.jobOperator.getJobNames();
        for (String jobName : jobNames) {
            log.warn("Job {} is in context. Trying to close...", jobName);
            if (!jobsMap.containsKey(jobName)) continue;
            JobExecution lastExecution = this.jobRepository.getLastJobExecution(jobName, jobsMap.get(jobName));
            if (lastExecution == null || lastExecution.getExitStatus().getExitCode().equals("COMPLETED")) continue;
            log.warn("Stopping job with Id : {}. Return value {}", lastExecution.getId(), this.jobOperator.stop(lastExecution.getId()));
            while (!this.jobRepository.getLastJobExecution(jobName, jobsMap.get(jobName)).getExitStatus().getExitCode().equals("STOPPED")) {
                log.warn(this.jobRepository.getLastJobExecution(jobName, jobsMap.get(jobName)).getExitStatus());
                Thread.sleep(70L);
            }
        }
    }
}
