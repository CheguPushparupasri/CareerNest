package com.careernest.modules.audit.service;

import com.careernest.modules.audit.entity.AuditLog;
import com.careernest.modules.audit.repository.AuditLogRepository;
import com.careernest.modules.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void record(User user, User actor, String eventType, String description, String ipAddress, String userAgent) {
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setActor(actor);
        log.setEventType(eventType);
        log.setEventDescription(description);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        auditLogRepository.save(log);
    }
}
