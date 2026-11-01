package com.careernest.modules.dashboard.controller;

import com.careernest.common.dto.ApiResponse;
import com.careernest.modules.dashboard.dto.DashboardSummaryResponse;
import com.careernest.modules.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ApiResponse<DashboardSummaryResponse> summary() {
        return ApiResponse.success("Dashboard summary fetched", dashboardService.summary());
    }
}
