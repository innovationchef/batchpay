package com.innovationchef.service;

import com.innovationchef.exception.ApiException;
import com.innovationchef.props.PayApiConnProp;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.UniformRandomBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;
import java.util.Map;

public class PayApiRetryTemplate extends RetryTemplate implements InitializingBean {

    private final PayApiConnProp prop;
    private final Class<? extends ApiException>[] exceptions;

    @SafeVarargs
    public <T extends ApiException> PayApiRetryTemplate(PayApiConnProp prop, Class<T>... ex) {
        this.prop = prop;
        this.exceptions = ex;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.setBackOffPolicy(backOffPolicyWithJitter());
        this.setRetryPolicy(retryPolicy());
    }

    private BackOffPolicy backOffPolicyWithJitter() {
        UniformRandomBackOffPolicy policy = new UniformRandomBackOffPolicy();
        policy.setMaxBackOffPeriod(this.prop.getRetry().getMaxBackoff());
        policy.setMinBackOffPeriod(this.prop.getRetry().getMinBackoff());
        return policy;
    }

    private Map<Class<? extends Throwable>, Boolean> includedExceptions() {
        Map<Class<? extends Throwable>, Boolean> includedExceptions = new HashMap<>();
        for (Class<? extends ApiException> exception : this.exceptions) {
            includedExceptions.put(exception, true);
        }
        return includedExceptions;
    }

    private RetryPolicy retryPolicy() {
        return new SimpleRetryPolicy(this.prop.getRetry().getMaxAttempts(), includedExceptions());
    }
}
