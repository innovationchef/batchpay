package com.innovationchef.support;

import com.innovationchef.constant.PaymentStatus;

import javax.persistence.AttributeConverter;

public class PaymentStatusConverter implements AttributeConverter<PaymentStatus, String> {
    @Override
    public String convertToDatabaseColumn(PaymentStatus status) {
        return status.getDef();
    }

    @Override
    public PaymentStatus convertToEntityAttribute(String def) {
        return PaymentStatus.getStatus(def);
    }
}
