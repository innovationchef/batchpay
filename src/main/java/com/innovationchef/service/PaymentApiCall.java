package com.innovationchef.service;

import com.innovationchef.constant.PaymentStatus;
import com.innovationchef.exception.ApiException;
import lombok.extern.log4j.Log4j2;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class PaymentApiCall {

    private static final int MIN = 2;
    private static final int MAX = 15;

    @Retryable(value = ApiException.class)
    public PaymentStatus  pay() {
        Random r = new Random();
        try {
            TimeUnit.SECONDS.sleep(r.nextInt(MAX - MIN + 1) + MIN);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        if (r.nextInt(50) < 25) return PaymentStatus.ACTC;
        throw new ApiException();
    }

    @Recover
    public PaymentStatus recoverPay(ApiException ex) {
        return PaymentStatus.RJCT;
    }
}
