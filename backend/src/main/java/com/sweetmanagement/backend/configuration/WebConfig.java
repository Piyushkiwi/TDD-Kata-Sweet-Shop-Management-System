package com.sweetmanagement.backend.configuration; // Use your own package name

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 1. Path pattern to apply CORS to
                .allowedOrigins(
                        "http://localhost:5173"
                ) // 2. List of allowed origins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH") // 3. Allowed HTTP methods
                .allowedHeaders("*") // 4. Allow all headers
                .allowCredentials(true) // 5. Allow credentials (cookies, authorization headers)
                .maxAge(3600); // 6. Cache pre-flight response for 1 hour
    }
}