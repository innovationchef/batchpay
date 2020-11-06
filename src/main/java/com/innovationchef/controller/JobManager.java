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

import java.util.Map;
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
        for (Map.Entry<String, JobParameters> jobConfig : jobsMap.entrySet()) {
            JobParameters parameters = jobConfig.getValue();
            String jobName = jobConfig.getKey();
            log.warn("Job {} is in context. Trying to close...", jobName);
            if (!jobsMap.containsKey(jobName)) continue;
            JobExecution lastExecution = this.jobRepository.getLastJobExecution(jobName, parameters);
            if (lastExecution == null || lastExecution.getExitStatus().getExitCode().equals("COMPLETED")) continue;
            log.warn("Stopping job with Id : {}. Return value {}", lastExecution.getId(), this.jobOperator.stop(lastExecution.getId()));
            while (!this.jobRepository.getLastJobExecution(jobName, parameters).getExitStatus().getExitCode().equals("STOPPED")) {
                log.warn(this.jobRepository.getLastJobExecution(jobName, parameters).getExitStatus());
                Thread.sleep(70L);
            }
        }
    }
}
