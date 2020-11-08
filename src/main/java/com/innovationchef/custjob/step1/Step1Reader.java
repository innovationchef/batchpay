package com.innovationchef.custjob.step1;

import com.innovationchef.constant.BatchConstant;
import com.innovationchef.entity.CustomerCSV;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;

public class Step1Reader extends FlatFileItemReader<CustomerCSV> {
    public Step1Reader() {
        super();
        DefaultLineMapper<CustomerCSV> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer(BatchConstant.DELIMITER));
        lineMapper.setFieldSetMapper(new FieldMapper());
        this.setLineMapper(lineMapper);
        this.setLinesToSkip(BatchConstant.SKIP_HEADER_COUNT);
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        JobParameters parameters = stepExecution.getJobParameters();
        String filePath = parameters.getString(BatchConstant.INPUT_FILE);
        assert filePath != null;
        this.setResource(new FileSystemResource(filePath));
    }
}
