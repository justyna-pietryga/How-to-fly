package com.justyna.project.config;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableNeo4jRepositories("com.justyna.project.repositories.graph")
@EnableTransactionManagement
@EntityScan(basePackages = "com.justyna.project.model.graph")
//        sessionFactoryRef = "getSessionFactory",
//        transactionManagerRef = "graphTransactionManager",
public class DB_GraphConfig {

    @Bean
    public SessionFactory sessionFactory() {
        System.out.println("sessionFactory");
        return new SessionFactory(configuration(),  "com.justyna.project.model.graph");
    }

    @Bean
    public Neo4jTransactionManager transactionManager() throws Exception {
        System.out.println("transactionMenager");
        return new Neo4jTransactionManager(sessionFactory());
    }

    @Bean
    public org.neo4j.ogm.config.Configuration configuration() {
        System.out.println("weszlo to dziadostwo");
        return new org.neo4j.ogm.config.Configuration.Builder()
                .uri("bolt://localhost")
                .credentials("neo4j", "secret")
//                .encryptionLevel("REQUIRED")
                //.uri("file:///var/tmp/neo4j.db")
                .build();
    }

}