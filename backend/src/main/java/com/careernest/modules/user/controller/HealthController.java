package com.careernest.modules.user.controller;

import com.careernest.common.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {
    @GetMapping("/api/v1/health")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success("CareerNest API is healthy", Map.of("status", "UP"));
    }
}
