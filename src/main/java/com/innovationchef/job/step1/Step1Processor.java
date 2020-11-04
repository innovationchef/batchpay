package com.innovationchef.job.step1;

import com.innovationchef.entity.Pain001CSV;
import org.springframework.batch.item.ItemProcessor;

public class Step1Processor implements ItemProcessor<Pain001CSV, Pain001CSV> {
    @Override
    public Pain001CSV process(Pain001CSV pain001CSV) throws Exception {
        return pain001CSV;
    }
}
