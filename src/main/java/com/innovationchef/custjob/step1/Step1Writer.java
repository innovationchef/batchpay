package com.innovationchef.custjob.step1;

import com.innovationchef.entity.CustomerCSV;
import org.hibernate.SessionFactory;
import org.springframework.batch.item.database.HibernateItemWriter;

public class Step1Writer extends HibernateItemWriter<CustomerCSV> {

    public Step1Writer(SessionFactory sessionFactory) {
        super();
        setSessionFactory(sessionFactory);
    }
}
