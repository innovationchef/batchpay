package com.innovationchef.batchcommons;

import com.innovationchef.constant.BatchConstant;
import org.springframework.batch.core.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ArchiveFileTasklet implements Tasklet, StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        ExitStatus endStatus = new ExitStatus(BatchConstant.EXIT_STATUS_END);
        if (stepExecution.getExitStatus().equals(endStatus))
            return endStatus;
        return null;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        JobParameters jobParameters = chunkContext.getStepContext().getStepExecution().getJobParameters();
        String inputFilepath = jobParameters.getString(BatchConstant.INPUT_FILE);
        String archiveFilepath = jobParameters.getString(BatchConstant.ARCHIVE_FILE);
        assert inputFilepath != null;
        assert archiveFilepath != null;
        Files.copy(Paths.get(inputFilepath), Paths.get(archiveFilepath), StandardCopyOption.REPLACE_EXISTING);
        return RepeatStatus.FINISHED;
    }
}
