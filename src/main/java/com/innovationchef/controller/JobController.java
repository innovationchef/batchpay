package com.innovationchef.controller;

import com.innovationchef.constant.BatchConstant;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
@RestController
public class JobController {

    @Value("${batchpay.config.filepath}")
    private String filepath;

    @Autowired
    private Job job;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobOperator jobOperator;

    @Autowired
    private ConcurrentHashMap<String, JobParameters> jobsMap;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping("v1/api/start/{jobId}")
    public Map<String, String> start(@PathVariable("jobId") String jobId) throws Exception {
        JobParameters parameters = new JobParametersBuilder()
                .addString(BatchConstant.JOB_ID, jobId)
                .addString(BatchConstant.INPUT_FILE, filepath)
                .toJobParameters();

        JobExecution lastExecution = this.jobRepository.getLastJobExecution(job.getName(), parameters);
        if (lastExecution != null) {
            if (lastExecution.getExitStatus().getExitCode().equals("STOPPED"))
                this.jobOperator.restart(lastExecution.getId());
        } else {
            this.jobLauncher.run(job, parameters);
        }
        this.jobsMap.put(this.job.getName(), parameters);

        HashMap<String, String> response = new HashMap<>();
        response.put("status", "started");
        response.put("jobId", jobId);
        return response;
    }
}
