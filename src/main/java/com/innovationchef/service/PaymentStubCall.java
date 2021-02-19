package com.innovationchef.service;

import com.innovationchef.constant.PaymentStatus;
import com.innovationchef.exception.ApiException;
import com.innovationchef.exception.RetryableApiException;
import lombok.extern.log4j.Log4j2;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Log4j2
public class PaymentStubCall implements PaymentService {

    private static final int MIN = 1;
    private static final int MAX = 50;
    private static final Random RANDOM = new Random();

    private final PayApiRetryTemplate retryTemplate;

    public PaymentStubCall(PayApiRetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
    }

    public PaymentStatus post() {
        try {
            TimeUnit.MILLISECONDS.sleep(RANDOM.nextInt(MAX - MIN + 1) + MIN);
        } catch (InterruptedException e) {
            throw new ApiException("Stub thread error");
        }
        return this.retryTemplate.execute(arg -> this.randomPost());
    }

    private PaymentStatus randomPost() {
        if (RANDOM.nextInt(50) < 40) return PaymentStatus.ACTC;
        throw new RetryableApiException("Manufactured retryable exception");
    }
}
