package com.careernest.modules.assistant.controller;

import com.careernest.common.dto.ApiResponse;
import com.careernest.modules.assistant.dto.AssistantRequest;
import com.careernest.modules.assistant.dto.AssistantResponse;
import com.careernest.modules.assistant.service.AssistantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assistant")
@Tag(name = "AI Career Assistant")
public class AssistantController {
    private final AssistantService service;

    public AssistantController(AssistantService service) {
        this.service = service;
    }

    @PostMapping
    public ApiResponse<AssistantResponse> advise(@Valid @RequestBody AssistantRequest request) {
        return ApiResponse.success("Assistant guidance generated", service.advise(request));
    }
}
