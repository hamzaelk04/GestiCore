package org.example.config;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override protected Class<?>[] getRootConfigClasses()    { return new Class[]{RootConfig.class, DataSourceConfig.class}; }
    @Override protected Class<?>[] getServletConfigClasses() { return new Class[]{WebConfig.class}; }
    @Override protected String[]   getServletMappings()      { return new String[]{"/"}; }
}
