package com.innovationchef.controller;

import com.innovationchef.constant.BatchConstant;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
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
    private JobRepository jobRepository;

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
        JobExecution lastExecution = this.jobRepository.getLastJobExecution(this.payJob.getName(), parameters);
        if (lastExecution != null) {
            if (lastExecution.getExitStatus().getExitCode().equals("STOPPED")) {
                this.jobOperator.restart(lastExecution.getId());
                response.put("status", "restarted");
            }
        } else {
            this.jobLauncher.run(this.payJob, parameters);
            response.put("status", "started");
        }

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
        JobExecution lastExecution = this.jobRepository.getLastJobExecution(this.custJob.getName(), parameters);
        if (lastExecution != null) {
            if (lastExecution.getExitStatus().getExitCode().equals("STOPPED")) {
                this.jobOperator.restart(lastExecution.getId());
                response.put("status", "restarted");
            }
        } else {
            this.jobLauncher.run(this.custJob, parameters);
            response.put("status", "started");
        }

        return response;
    }
}
