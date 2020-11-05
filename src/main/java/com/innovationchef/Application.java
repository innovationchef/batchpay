package com.innovationchef;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class, ErrorMvcAutoConfiguration.class})
public class Application {
    public static void main(String... args) {
        SpringApplication.run(Application.class);
    }
}
