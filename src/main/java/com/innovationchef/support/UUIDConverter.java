package com.innovationchef.support;

import javax.persistence.AttributeConverter;
import java.util.UUID;

public class UUIDConverter implements AttributeConverter<UUID, String> {
    @Override
    public String convertToDatabaseColumn(UUID uuid) {
        return uuid.toString();
    }

    @Override
    public UUID convertToEntityAttribute(String uuid) {
        return UUID.fromString(uuid);
    }
}
