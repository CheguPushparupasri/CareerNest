package com.careernest.modules.notification.controller;

import com.careernest.common.dto.ApiResponse;
import com.careernest.modules.notification.dto.NotificationResponse;
import com.careernest.modules.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/unread")
    public ApiResponse<List<NotificationResponse>> getUnread() {
        return ApiResponse.success("Unread notifications fetched", notificationService.getUnreadNotifications());
    }

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getAll() {
        return ApiResponse.success("All notifications fetched", notificationService.getAllNotifications());
    }

    @GetMapping("/count")
    public ApiResponse<Long> getUnreadCount() {
        return ApiResponse.success("Unread count fetched", notificationService.getUnreadCount());
    }

    @PutMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ApiResponse.success("Notification marked as read");
    }

    @PutMapping("/read-all")
    public ApiResponse<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ApiResponse.success("All notifications marked as read");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        notificationService.delete(id);
        return ApiResponse.success("Notification deleted");
    }
}
