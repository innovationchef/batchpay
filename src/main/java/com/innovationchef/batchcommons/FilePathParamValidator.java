package com.innovationchef.batchcommons;

import com.innovationchef.constant.BatchConstant;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePathParamValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
        String fileName = jobParameters.getString(BatchConstant.INPUT_FILE);
        if (StringUtils.isEmpty(fileName))
            throw new JobParametersInvalidException("filepath value cannot be null");
        try {
            Path file = Paths.get(fileName);
            if (Files.notExists(file) || !Files.isReadable(file))
                throw new IOException("Error with input file for batch processing");
        } catch (IOException e) {
            throw new JobParametersInvalidException(e.getMessage());
        }
    }
}
