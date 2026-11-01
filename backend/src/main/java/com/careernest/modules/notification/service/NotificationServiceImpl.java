package com.careernest.modules.notification.service;

import com.careernest.common.exception.ResourceNotFoundException;
import com.careernest.common.exception.ForbiddenException;
import com.careernest.common.exception.UnauthorizedException;
import com.careernest.common.security.CustomUserDetails;
import com.careernest.modules.notification.dto.NotificationResponse;
import com.careernest.modules.notification.entity.Notification;
import com.careernest.modules.notification.repository.NotificationRepository;
import com.careernest.modules.user.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications() {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(currentUser().getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<NotificationResponse> getAllNotifications() {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUser().getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!notification.getUser().getId().equals(currentUser().getId())) {
            throw new ForbiddenException("Access denied");
        }
        notification.markAsRead();
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(currentUser().getId());
        unread.forEach(Notification::markAsRead);
        notificationRepository.saveAll(unread);
    }

    @Override
    @Transactional
    public void delete(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!notification.getUser().getId().equals(currentUser().getId())) {
            throw new ForbiddenException("Access denied");
        }
        notificationRepository.delete(notification);
    }

    @Override
    public long getUnreadCount() {
        return notificationRepository.countByUserIdAndIsReadFalse(currentUser().getId());
    }

    @Override
    @Transactional
    public void createNotification(String title, String message, String type, String actionUrl) {
        Notification notification = new Notification();
        notification.setUser(currentUser());
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setActionUrl(actionUrl);
        notificationRepository.save(notification);
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails details) {
            return details.getUser();
        }
        throw new UnauthorizedException("Authentication is required");
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.getActionUrl(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
