package com.innovationchef.custjob.step1;

import com.innovationchef.entity.CustomerCSV;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemProcessor;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Log4j2
public class Step1Processor implements ItemProcessor<CustomerCSV, CustomerCSV> {

    private static final int MIN = 50;
    private static final int MAX = 200;

    @Override
    public CustomerCSV process(CustomerCSV customerCSV) throws Exception {
        log.info("Saving {}", customerCSV.getAccountNo());
        Random r = new Random();
        TimeUnit.MILLISECONDS.sleep(r.nextInt(MAX - MIN + 1) + MIN);
        return customerCSV;
    }
}
