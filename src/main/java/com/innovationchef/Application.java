package com.innovationchef;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableRetry
@EnableTransactionManagement
@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class Application {
    public static void main(String... args) {
        SpringApplication.run(Application.class);
    }
}
