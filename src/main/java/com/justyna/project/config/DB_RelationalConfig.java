package com.justyna.project.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.justyna.project.repositories.relational",
        entityManagerFactoryRef = "relationalDBFactory",
        transactionManagerRef = "relationalDBTransactionManager")
public class DB_RelationalConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties userDSProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    public DataSource userDS(@Qualifier("userDSProperties") DataSourceProperties userDSProperties) {
        return userDSProperties.initializeDataSourceBuilder().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean relationalDBFactory(@Qualifier("userDS") DataSource userDS, EntityManagerFactoryBuilder builder) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "create-drop");
        return builder.dataSource(userDS).packages("com.justyna.project.model.relational").properties(properties).build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager relationalDBTransactionManager(EntityManagerFactory relationalDBFactory) {
        return new JpaTransactionManager(relationalDBFactory);
    }

}
