package com.f1community.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile({"dev", "prod"})
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins[0]}")
    private String origin0;

    @Value("${cors.allowed-origins[1]}")
    private String origin1;

    @Value("${cors.allowed-origins[2]}")
    private String origin2;

    @Value("${cors.allowed-origins[3]}")
    private String origin3;

    @Value("${cors.allowed-origins[4]}")
    private String origin4;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(origin0, origin1, origin2, origin3, origin4)
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}