package com.example.demon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/auth/**")
                        .allowedOrigins("http://localhost:63342") // Разрешаем ваш фронтенд
                        .allowedMethods("POST", "GET", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
