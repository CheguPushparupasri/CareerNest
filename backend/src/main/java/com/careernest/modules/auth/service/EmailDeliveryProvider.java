package com.careernest.modules.auth.service;

public interface EmailDeliveryProvider {
    boolean isConfigured();
    void send(String recipient, String subject, String link);
}