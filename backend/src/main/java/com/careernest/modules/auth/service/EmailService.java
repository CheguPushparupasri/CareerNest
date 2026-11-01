package com.careernest.modules.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EmailService {
    private final String frontendUrl;
    private final EmailDeliveryProvider provider;

    public EmailService(@Value("${app.frontend-url}") String frontendUrl, EmailDeliveryProvider provider) {
        this.frontendUrl = frontendUrl;
        this.provider = provider;
    }

    public boolean isConfigured() {
        return provider.isConfigured();
    }

    public void sendVerificationEmail(String email, String token) {
        if (provider.isConfigured()) {
            provider.send(email, "Verify your CareerNest email", link("/verify-email", token));
        }
    }

    public void sendPasswordResetEmail(String email, String token) {
        if (provider.isConfigured()) {
            provider.send(email, "Reset your CareerNest password", link("/reset-password", token));
        }
    }

    private String link(String path, String token) {
        return UriComponentsBuilder.fromUriString(frontendUrl)
                .path(path)
                .queryParam("token", token)
                .build()
                .encode()
                .toUriString();
    }
}
