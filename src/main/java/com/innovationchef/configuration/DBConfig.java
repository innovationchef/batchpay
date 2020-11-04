package com.innovationchef.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

@Getter
@Setter
@Configuration
@EnableTransactionManagement
@ConfigurationProperties("batchpay.db")
public class DBConfig implements ApplicationListener<ContextClosedEvent> {

    public static final String ENTITY_PACKAGE = "com.innovationchef.entity";

    private String url;
    private String username;
    private String password;
    private String driver;
    private String testConnQuery;
    private String poolName;
    private String dialect;
    private String hbm2ddl;
    private int maxPoolSize;

    @Lazy
    @Autowired
    private LocalSessionFactoryBean sessionFactoryBean;

    @Bean
    public HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setPassword(password);
        config.setUsername(username);
        config.setDriverClassName(driver);
        config.setAutoCommit(false);
        config.setConnectionTestQuery(testConnQuery);
        config.setMaximumPoolSize(maxPoolSize);
        config.setPoolName(poolName);
        return new HikariDataSource(config);
    }

    @Bean
    public LocalSessionFactoryBean createSessionFactory() {
        HikariDataSource ds = createDataSource();
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", dialect);
        properties.setProperty("hibernate.hbm2ddl.auto", hbm2ddl);
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(ds);
        sessionFactoryBean.setPackagesToScan(ENTITY_PACKAGE);
        sessionFactoryBean.setHibernateProperties(properties);
        return sessionFactoryBean;
    }

    @Bean("hbrTxnMgr")
    public PlatformTransactionManager createTransactionManagerBean(LocalSessionFactoryBean factory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(factory.getObject());
        return transactionManager;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        this.sessionFactoryBean.destroy();
    }
}
