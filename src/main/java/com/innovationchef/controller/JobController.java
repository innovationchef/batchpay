package com.innovationchef.controller;

import com.innovationchef.constant.BatchConstant;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@RestController
public class JobController {

    @Value("${batchpay.config.filepath}")
    private String filepath;

    @Autowired
    @Qualifier("payJob")
    private Job payJob;

    @Autowired
    @Qualifier("custJob")
    private Job custJob;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private ConcurrentHashMap<String, JobParameters> jobsMap;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping("v1/api/pay/start/{jobId}/{fileId}")
    public Map<String, String> payJobStart(@PathVariable("jobId") String jobId,
                                           @PathVariable("fileId") String fileId) throws Exception {
        String completeFilePath = this.filepath + "payment-file-" + fileId + ".csv";
        final JobParameters parameters = new JobParametersBuilder()
                .addString(BatchConstant.JOB_TYPE, BatchConstant.PAYMENT_JOB, true)
                .addString(BatchConstant.JOB_ID, jobId, true)
                .addString(BatchConstant.INPUT_FILE, completeFilePath)
                .toJobParameters();

        HashMap<String, String> response = new HashMap<>();
        response.put("jobId", jobId);
        response.put("fileId", completeFilePath);

        Set<JobExecution> executions = this.jobExplorer.findRunningJobExecutions(this.payJob.getName());
        if (executions.size() > 0) {
            response.put("status", "already-running");
            return response;
        }

        this.jobsMap.put(this.payJob.getName(), parameters);
        JobInstance lastJobInstance = this.jobExplorer.getLastJobInstance(this.payJob.getName());
        if (lastJobInstance != null) {
            JobExecution lastExecution = this.jobExplorer.getLastJobExecution(lastJobInstance);
            if (lastExecution.getExitStatus().getExitCode().equals("STOPPED")) {
                this.jobOperator.restart(lastExecution.getId());
                response.put("status", "restarted");
                return response;
            }
        }
        this.runJob(this.payJob, parameters);
        response.put("status", "started");

        return response;
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
        @GetMapping("v1/api/cust/start/{jobId}/{fileId}")
    public Map<String, String> custJobStart(@PathVariable("jobId") String jobId,
                                            @PathVariable("fileId") String fileId) throws Exception {
        String completeFilePath = this.filepath + "customer-file-" + fileId + ".csv";
        final JobParameters parameters = new JobParametersBuilder()
                .addString(BatchConstant.JOB_TYPE, BatchConstant.CUSTOMER_JOB, true)
                .addString(BatchConstant.JOB_ID, jobId, true)
                .addString(BatchConstant.INPUT_FILE, completeFilePath)
                .toJobParameters();

        HashMap<String, String> response = new HashMap<>();
        response.put("jobId", jobId);
        response.put("fileId", completeFilePath);

        Set<JobExecution> executions = this.jobExplorer.findRunningJobExecutions(this.custJob.getName());
        if (executions.size() > 0) {
            response.put("status", "already-running");
            return response;
        }

        this.jobsMap.put(this.custJob.getName(), parameters);
        JobInstance lastJobInstance = this.jobExplorer.getLastJobInstance(this.payJob.getName());
        if (lastJobInstance != null) {
            JobExecution lastExecution = this.jobExplorer.getLastJobExecution(lastJobInstance);
            if (lastExecution.getExitStatus().getExitCode().equals("STOPPED")) {
                this.jobOperator.restart(lastExecution.getId());
                response.put("status", "restarted");
                return response;
            }
        }
        this.runJob(this.custJob, parameters);
        response.put("status", "started");

        return response;
    }

    private void runJob(Job job, JobParameters parameters) {
        try {
            JobExecution jobExecution = this.jobLauncher.run(job, parameters);
        } catch (JobExecutionAlreadyRunningException e) {
            log.info("Job with fileName = {} is already running.", parameters.getParameters().get(BatchConstant.INPUT_FILE));
        } catch (JobRestartException e) {
            log.info("Job with fileName = {} was not restarted.", parameters.getParameters().get(BatchConstant.INPUT_FILE));
        } catch (JobInstanceAlreadyCompleteException e) {
            log.info("Job with fileName = {} already completed.", parameters.getParameters().get(BatchConstant.INPUT_FILE));
        } catch (JobParametersInvalidException e) {
            log.info("Invalid job parameters: {}", parameters.getParameters());
        }
    }
}
