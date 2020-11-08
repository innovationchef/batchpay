package com.innovationchef.payjob.step1;

import com.innovationchef.entity.Pain001CSV;
import org.hibernate.SessionFactory;
import org.springframework.batch.item.database.HibernateItemWriter;

public class Step1Writer extends HibernateItemWriter<Pain001CSV> {

    public Step1Writer(SessionFactory sessionFactory) {
        super();
        setSessionFactory(sessionFactory);
    }
}
