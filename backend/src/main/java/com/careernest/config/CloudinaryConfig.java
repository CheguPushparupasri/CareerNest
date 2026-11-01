package com.careernest.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Bean
    @ConditionalOnProperty(name = "app.cloudinary.enabled", havingValue = "true")
    Cloudinary cloudinary(@Value("${app.cloudinary.cloud-name}") String cloudName,
                          @Value("${app.cloudinary.api-key}") String apiKey,
                          @Value("${app.cloudinary.api-secret}") String apiSecret) {
        if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
            throw new IllegalStateException("Cloudinary credentials are required when CLOUDINARY_ENABLED is true");
        }
        return new Cloudinary(Map.of("cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret, "secure", true));
    }
}
