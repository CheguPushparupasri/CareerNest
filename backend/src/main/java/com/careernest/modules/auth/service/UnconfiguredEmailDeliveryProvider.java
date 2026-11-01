package com.careernest.modules.auth.service;

public class UnconfiguredEmailDeliveryProvider implements EmailDeliveryProvider {
    @Override
    public boolean isConfigured() {
        return false;
    }

    @Override
    public void send(String recipient, String subject, String link) {
        throw new IllegalStateException("Email delivery is not configured");
    }
}