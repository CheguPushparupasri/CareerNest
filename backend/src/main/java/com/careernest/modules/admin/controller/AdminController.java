package com.careernest.modules.admin.controller;

import com.careernest.common.dto.ApiResponse;
import com.careernest.modules.admin.dto.AdminAuditLogResponse;
import com.careernest.modules.admin.dto.AdminOverviewResponse;
import com.careernest.modules.admin.dto.AdminUserResponse;
import com.careernest.modules.admin.service.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin")
public class AdminController {
    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    @GetMapping("/overview")
    public ApiResponse<AdminOverviewResponse> overview() {
        return ApiResponse.success("Admin overview fetched", service.overview());
    }

    @GetMapping("/users")
    public ApiResponse<Page<AdminUserResponse>> getUsers(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success("Users fetched", service.getUsers(query, pageable));
    }

    @GetMapping("/audit-logs")
    public ApiResponse<Page<AdminAuditLogResponse>> getAuditLogs(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success("Audit logs fetched", service.getAuditLogs(query, pageable));
    }
}
