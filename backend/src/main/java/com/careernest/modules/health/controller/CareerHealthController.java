package com.careernest.modules.health.controller;

import com.careernest.common.dto.ApiResponse;
import com.careernest.modules.health.dto.CareerHealthResponse;
import com.careernest.modules.health.service.CareerHealthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health-score")
@Tag(name = "Career Health")
public class CareerHealthController {
    private final CareerHealthService service;

    public CareerHealthController(CareerHealthService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<CareerHealthResponse> getHealth() {
        return ApiResponse.success("Career health fetched successfully", service.getHealth());
    }
}
