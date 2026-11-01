package com.careernest.modules.admin.service;

import com.careernest.modules.admin.dto.AdminAuditLogResponse;
import com.careernest.modules.admin.dto.AdminOverviewResponse;
import com.careernest.modules.admin.dto.AdminUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    AdminOverviewResponse overview();
    Page<AdminUserResponse> getUsers(String query, Pageable pageable);
    Page<AdminAuditLogResponse> getAuditLogs(String query, Pageable pageable);
}
