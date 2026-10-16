package org.example.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        HikariConfig cfg = new HikariConfig();
        // File DB: survives restarts, and IntelliJ can connect while Tomcat runs (AUTO_SERVER)
        cfg.setJdbcUrl("jdbc:h2:file:" + System.getProperty("user.home") + "/gesticore/db;AUTO_SERVER=TRUE");
        cfg.setDriverClassName("org.h2.Driver");
        cfg.setUsername("sa");
        cfg.setPassword("");
        cfg.setMaximumPoolSize(10);
        return new HikariDataSource(cfg);
    }
}
