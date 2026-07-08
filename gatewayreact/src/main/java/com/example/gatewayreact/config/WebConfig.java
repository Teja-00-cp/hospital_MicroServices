package com.example.gatewayreact.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebConfig implements WebFluxConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Tells the gateway to serve any static resource requested at the root level
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}