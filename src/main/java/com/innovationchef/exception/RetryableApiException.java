package com.innovationchef.exception;

public class RetryableApiException extends ApiException {

    public RetryableApiException(String message) {
        super(message);
    }
}
