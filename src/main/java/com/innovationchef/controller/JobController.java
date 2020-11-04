package com.innovationchef.controller;

import com.innovationchef.constant.BatchConstant;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class JobController {

    @Value("${batchpay.config.filepath}")
    private String filepath;

    @Autowired
    private Job job;

    @Autowired
    private JobLauncher launcher;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping("v1/api/start")
    public Map<String, String> start() throws Exception {
        JobParameters parameters = new JobParametersBuilder()
                .addString(BatchConstant.JOB_ID, String.valueOf(System.currentTimeMillis()))
                .addString(BatchConstant.INPUT_FILE, filepath)
                .toJobParameters();
        this.launcher.run(job, parameters);
        return Collections.singletonMap("status", "started");
    }
}
