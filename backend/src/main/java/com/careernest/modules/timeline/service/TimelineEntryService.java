package com.careernest.modules.timeline.service;

import com.careernest.modules.timeline.dto.TimelineEntryRequest;
import com.careernest.modules.timeline.dto.TimelineEntryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TimelineEntryService {
    TimelineEntryResponse create(TimelineEntryRequest request);
    TimelineEntryResponse update(Long id, TimelineEntryRequest request);
    TimelineEntryResponse getById(Long id);
    Page<TimelineEntryResponse> getAll(Pageable pageable);
    Page<TimelineEntryResponse> search(String query, Pageable pageable);
    Page<TimelineEntryResponse> getByType(String type, Pageable pageable);
    void delete(Long id);
}
