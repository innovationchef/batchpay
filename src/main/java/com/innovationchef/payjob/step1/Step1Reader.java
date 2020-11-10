package com.innovationchef.payjob.step1;

import com.innovationchef.constant.BatchConstant;
import com.innovationchef.entity.Pain001CSV;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;

class Step1Reader extends FlatFileItemReader<Pain001CSV> {

    public Step1Reader(JobParameters parameters) {
        super();
        DefaultLineMapper<Pain001CSV> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(new DelimitedLineTokenizer(BatchConstant.DELIMITER));
        lineMapper.setFieldSetMapper(new FieldMapper());
        this.setLineMapper(lineMapper);
        this.setLinesToSkip(BatchConstant.SKIP_HEADER_COUNT);
        String filePath = parameters.getString(BatchConstant.INPUT_FILE);
        assert filePath != null;
        this.setResource(new FileSystemResource(filePath));
    }
}
