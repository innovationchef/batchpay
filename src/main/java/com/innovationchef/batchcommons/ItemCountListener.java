package com.innovationchef.batchcommons;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

@Log4j2
public class ItemCountListener implements ChunkListener {

    @Override
    public void beforeChunk(ChunkContext context) {
        log.info("Process started for new chunk");
    }

    @Override
    public void afterChunk(ChunkContext context) {
        int readCount = context.getStepContext().getStepExecution().getReadCount();
        int skipCount = context.getStepContext().getStepExecution().getSkipCount();
        log.info("Processed/Skip ItemCount: {}/{}", readCount, skipCount);
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        int readCount = context.getStepContext().getStepExecution().getReadCount();
        int skipCount = context.getStepContext().getStepExecution().getSkipCount();
        log.info("Process error in last chunk after processing {} and skipping {} items. Error recorded: {}", readCount, skipCount, context.getAttribute(ChunkListener.ROLLBACK_EXCEPTION_KEY));
    }
}
