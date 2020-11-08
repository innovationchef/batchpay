package com.innovationchef.payjob.step1;

import com.innovationchef.entity.Pain001CSV;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

class FieldMapper implements FieldSetMapper<Pain001CSV> {

    @Override
    public Pain001CSV mapFieldSet(FieldSet fieldSet) throws BindException {
        return Pain001CSV.builder()
                .forPayment(fieldSet.readRawString(0), fieldSet.readRawString(5))
                .from(fieldSet.readRawString(3))
                .to(fieldSet.readRawString(4))
                .forTxnAmt(fieldSet.readBigDecimal(7), fieldSet.readRawString(6))
                .toBeExecOn(fieldSet.readRawString(2))
                .withBearer(fieldSet.readRawString(1))
                .build();
    }
}
