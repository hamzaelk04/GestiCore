package org.example.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Configuration
@Import({PersistenceConfig.class, PasswordConfig.class, DataInitializer.class})
@ComponentScan(
        basePackages = {"org.example.service", "org.example.mapper"},
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = {Controller.class, ControllerAdvice.class}))
public class RootConfig {
}
