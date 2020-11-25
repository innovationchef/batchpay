package com.innovationchef.controller;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Component
public class JobManager implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private ConcurrentHashMap<String, JobParameters> jobsMap;

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        for (Map.Entry<String, JobParameters> jobConfig : this.jobsMap.entrySet()) {
            log.warn("Job {} is in context. Trying to close...", jobConfig.getKey());
            shutdownJob(jobConfig.getKey(), jobConfig.getValue());
        }
    }

    private void shutdownJob(String jobName, JobParameters parameters)
            throws NoSuchJobExecutionException, JobExecutionNotRunningException, InterruptedException {
        if (!this.jobsMap.containsKey(jobName)) return;
        JobInstance lastInstance = this.jobExplorer.getLastJobInstance(jobName);
        if (lastInstance == null) return;
        JobExecution lastExecution = this.jobExplorer.getLastJobExecution(lastInstance);
        if (lastExecution.getExitStatus().getExitCode().equals("COMPLETED")) return;
        log.warn("Stopping job with Id : {}. Return value {}", lastExecution.getId(), this.jobOperator.stop(lastExecution.getId()));
        while (!this.jobExplorer.getLastJobExecution(lastInstance).getExitStatus().getExitCode().equals("STOPPED")) {
            log.warn(this.jobExplorer.getLastJobExecution(lastInstance).getExitStatus());
            Thread.sleep(70L);
        }
    }
}
