package com.innovationchef.custjob.step1;

import com.innovationchef.entity.CustomerCSV;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

class FieldMapper implements FieldSetMapper<CustomerCSV> {

    @Override
    public CustomerCSV mapFieldSet(FieldSet fieldSet) throws BindException {
        CustomerCSV customerCSV = new CustomerCSV();
        customerCSV.setAccountNo(fieldSet.readRawString(0));
        customerCSV.setAccountStatus(fieldSet.readRawString(3));
        customerCSV.setAction(fieldSet.readRawString(6));
        customerCSV.setCountry(fieldSet.readRawString(4));
        customerCSV.setCurrency(fieldSet.readRawString(5));
        customerCSV.setInterestRate(fieldSet.readRawString(2));
        customerCSV.setName(fieldSet.readRawString(1));
        return customerCSV;
    }
}
