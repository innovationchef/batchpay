package com.innovationchef.support;

import com.innovationchef.constant.ChargeBearer;

import javax.persistence.AttributeConverter;

public class ChargeBearerConverter implements AttributeConverter<ChargeBearer, String> {
    @Override
    public String convertToDatabaseColumn(ChargeBearer bearer) {
        return bearer.getDef();
    }

    @Override
    public ChargeBearer convertToEntityAttribute(String def) {
        return ChargeBearer.getBearerType(def);
    }
}
