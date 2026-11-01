package com.careernest.config;

import com.careernest.modules.auth.service.EmailDeliveryProvider;
import com.careernest.modules.auth.service.UnconfiguredEmailDeliveryProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailProviderConfig {
    @Bean
    @ConditionalOnMissingBean(EmailDeliveryProvider.class)
    EmailDeliveryProvider unconfiguredEmailDeliveryProvider() {
        return new UnconfiguredEmailDeliveryProvider();
    }
}