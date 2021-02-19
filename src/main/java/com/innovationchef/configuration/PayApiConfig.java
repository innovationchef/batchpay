package com.innovationchef.configuration;

import com.innovationchef.exception.RetryableApiException;
import com.innovationchef.props.PayApiConnProp;
import com.innovationchef.service.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PayApiConnProp.class)
public class PayApiConfig {

    @Bean
    public PayApiRestTemplate payApiRestTemplate(PayApiConnProp prop) {
        return new PayApiRestTemplate(prop);
    }

    @Bean
    public PayApiRetryTemplate payApiRetryTemplate(PayApiConnProp prop) {
        return new PayApiRetryTemplate(prop, RetryableApiException.class);
    }

    @Bean
    @ConditionalOnProperty(name = "batchpay.config.is-api-available", havingValue = "true")
    public PaymentService apiCallService(PayApiConnProp prop,
                                         PayApiRestTemplate restTemplate,
                                         PayApiRetryTemplate retryTemplate) {
        return new PaymentApiCall(prop, restTemplate, retryTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "batchpay.config.is-api-available", havingValue = "false", matchIfMissing = true)
    public PaymentService stubCallService(PayApiRetryTemplate retryTemplate) {
        return new PaymentStubCall(retryTemplate);
    }
}
