package com.careernest.modules.assistant.service;

import com.careernest.modules.assistant.dto.AssistantRequest;
import com.careernest.modules.assistant.dto.AssistantResponse;

public interface AssistantService {
    AssistantResponse advise(AssistantRequest request);
}
