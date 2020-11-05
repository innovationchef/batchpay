package com.innovationchef.job.step1;

import com.innovationchef.constant.PaymentStatus;
import com.innovationchef.entity.Pain001CSV;
import com.innovationchef.service.PaymentApiCall;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemProcessor;

@Log4j2
public class Step1Processor implements ItemProcessor<Pain001CSV, Pain001CSV> {

    private PaymentApiCall apiCall;

    public Step1Processor(PaymentApiCall apiCall) {
        this.apiCall = apiCall;
    }

    @Override
    public Pain001CSV process(Pain001CSV pain001CSV) throws Exception {
        log.info("Executing {}", pain001CSV.getPaymentId());
        PaymentStatus status = apiCall.pay();
        pain001CSV.setStatus(status);
        return pain001CSV;
    }
}
