package com.innovationchef.controller;

import com.innovationchef.batchcommons.JobManager;
import com.innovationchef.constant.BatchConstant;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
public class PayJobController extends BaseController {

    private String filepath;

    public PayJobController(@Value("${batchpay.config.filepath}") String filepath,
                            @Qualifier(BatchConstant.PAY_JOB_NAME) Job job,
                            JobManager jobManager) throws DuplicateJobException {
        super(job, jobManager);
        this.filepath = filepath;
    }

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
        this.runJob(parameters);
        HashMap<String, String> response = new HashMap<>();
        response.put("jobId", jobId);
        response.put("fileId", completeFilePath);
        response.put("status", "started");
        return response;
    }
}
