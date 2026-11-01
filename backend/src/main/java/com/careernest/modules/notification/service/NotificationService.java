package com.careernest.modules.notification.service;

import com.careernest.modules.notification.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getUnreadNotifications();
    List<NotificationResponse> getAllNotifications();
    void markAsRead(Long notificationId);
    void markAllAsRead();
    void delete(Long notificationId);
    long getUnreadCount();
    void createNotification(String title, String message, String type, String actionUrl);
}
