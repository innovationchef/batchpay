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
        int count = context.getStepContext().getStepExecution().getReadCount();
        log.info("Processed ItemCount: {}", count);
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        int count = context.getStepContext().getStepExecution().getReadCount();
        log.info("Process error in last chunk after processing {} items", count);
    }
}
