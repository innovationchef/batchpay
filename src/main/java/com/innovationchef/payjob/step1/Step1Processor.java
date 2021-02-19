package com.innovationchef.payjob.step1;

import com.innovationchef.constant.PaymentStatus;
import com.innovationchef.entity.Pain001CSV;
import com.innovationchef.service.PaymentApiCall;
import com.innovationchef.service.PaymentService;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.item.ItemProcessor;

@Log4j2
public class Step1Processor implements ItemProcessor<Pain001CSV, Pain001CSV> {

    private PaymentService service;

    public Step1Processor(PaymentService service) {
        this.service = service;
    }

    @Override
    public Pain001CSV process(Pain001CSV pain001CSV) throws Exception {
        log.info("Executing {}", pain001CSV.getPaymentId());
        PaymentStatus status = this.service.post();
        pain001CSV.setStatus(status);
        return pain001CSV;
    }
}
