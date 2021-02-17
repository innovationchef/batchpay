package com.innovationchef.batchcommons;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Component
public class JobManager implements ApplicationListener<ContextClosedEvent> {

    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;
    private final JobRegistry jobRegistry;
    private final JobLauncher jobLauncher;

    private final ConcurrentHashMap<String, JobParameters> jobsMap = new ConcurrentHashMap<>();

    public JobManager(JobExplorer jobExplorer,
                      JobOperator jobOperator,
                      JobRegistry jobRegistry,
                      JobLauncher jobLauncher) {
        this.jobExplorer = jobExplorer;
        this.jobOperator = jobOperator;
        this.jobRegistry = jobRegistry;
        this.jobLauncher = jobLauncher;
    }

    public void registerJob(final Job job) throws DuplicateJobException {
        this.jobRegistry.register(new JobFactory() {
            @Override
            public Job createJob() {
                return job;
            }

            @Override
            public String getJobName() {
                return job.getName();
            }
        });
    }

    public Optional<JobExecution> runJob(final Job job, final JobParameters jobParameters) {
        this.jobsMap.put(job.getName(), jobParameters);
        if (this.isJobAlreadyRunning(job)) {
            return Optional.empty();
        }
        if (this.shouldJobBeRestarted(job)) {
            return this.runInterruptedJob(job);
        }
        return this.runFreshJob(job, jobParameters);
    }

    private Optional<JobExecution> runInterruptedJob(final Job job) {
        JobInstance lastJobInstance = this.jobExplorer.getLastJobInstance(job.getName());
        JobExecution lastExecution = this.jobExplorer.getLastJobExecution(lastJobInstance);
        try {
            long executionId = this.jobOperator.restart(lastExecution.getId());
            JobExecution newExecution = this.jobExplorer.getLastJobExecution(lastJobInstance);
            return Optional.of(newExecution);
        } catch (JobInstanceAlreadyCompleteException e) {
            log.error("Job: {} is already complete with execution if: {}", job.getName(), lastExecution.getId());
        } catch (NoSuchJobExecutionException e) {
            log.error("Job execution {} for {} job does not exist", lastExecution.getId(), job.getName());
        } catch (NoSuchJobException e) {
            log.error("Job: {} does not exist", job.getName());
        } catch (JobRestartException e) {
            log.error("Job: {} could not be restarted", job.getName());
        } catch (JobParametersInvalidException e) {
            log.error("Invalid parameters: {} provided for job: {}", lastExecution.getJobParameters(), job.getName());
        }
        return Optional.empty();
    }

    private Optional<JobExecution> runFreshJob(final Job job, final JobParameters jobParameters) {
        try {
            return Optional.of(this.jobLauncher.run(job, jobParameters));
        } catch (JobExecutionAlreadyRunningException e) {
            log.error("Job: {} is already running", job.getName());
        } catch (JobRestartException e) {
            log.error("Job: {} restart attempt failed", job.getName());
        } catch (JobInstanceAlreadyCompleteException e) {
            log.error("Job: {} instance trying to restart is already complete", job.getName());
        } catch (JobParametersInvalidException e) {
            log.error("Job: {} parameters are invalid", jobParameters);
        }
        return Optional.empty();
    }

    private boolean isJobAlreadyRunning(final Job job) {
        Set<JobExecution> executions = this.jobExplorer.findRunningJobExecutions(job.getName());
        return !executions.isEmpty();
    }

    private boolean shouldJobBeRestarted(final Job job) {
        JobInstance lastInstance = this.jobExplorer.getLastJobInstance(job.getName());
        if (lastInstance != null) {
            JobExecution lastJobExecution = this.jobExplorer.getLastJobExecution(lastInstance);
            return lastJobExecution.getExitStatus().getExitCode().equals("STOPPED");
        }
        return false;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        for (Map.Entry<String, JobParameters> jobConfig : this.jobsMap.entrySet()) {
            log.warn("Job {} is in context. Trying to close...", jobConfig.getKey());
            try {
                shutdownJob(jobConfig.getKey());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void shutdownJob(final String jobName)
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
