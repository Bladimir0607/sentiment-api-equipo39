package com.hackaton.sentiment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapea la carpeta generada por Maven a la URL /docs/**
        registry.addResourceHandler("/docs/**")
                .addResourceLocations("file:target/site/");
    }
}
