package com.innovationchef.service;

import com.innovationchef.constant.PaymentStatus;
import com.innovationchef.props.PayApiConnProp;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;

@Log4j2
public class PaymentApiCall implements PaymentService {

    private final PayApiConnProp prop;
    private final PayApiRestTemplate restTemplate;
    private final PayApiRetryTemplate retryTemplate;

    public PaymentApiCall(PayApiConnProp prop,
                          PayApiRestTemplate restTemplate,
                          PayApiRetryTemplate retryTemplate) {
        this.prop = prop;
        this.restTemplate = restTemplate;
        this.retryTemplate = retryTemplate;
    }

    public PaymentStatus post() {
        return this.retryTemplate.execute(arg -> this.callApi());
    }

    public PaymentStatus callApi() {
        ResponseEntity<String> response = this.restTemplate.getForEntity("random-url", String.class);
        return PaymentStatus.valueOf(response.getBody());
    }
}
