package com.careernest.modules.timeline.repository;

import com.careernest.modules.timeline.entity.TimelineEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface TimelineEntryRepository extends JpaRepository<TimelineEntry, Long> {
    Page<TimelineEntry> findByUserIdOrderByEntryDateDesc(Long userId, Pageable pageable);

    @Query("select t from TimelineEntry t where t.user.id = :userId and lower(concat(t.title, ' ', coalesce(t.description, ''), ' ', coalesce(t.organization, ''))) like lower(concat('%', :query, '%'))")
    Page<TimelineEntry> searchEntries(@Param("userId") Long userId, @Param("query") String query, Pageable pageable);

    Page<TimelineEntry> findByUserIdAndEntryType(Long userId, String entryType, Pageable pageable);

    List<TimelineEntry> findTop5ByUserIdAndEntryDateAfterOrderByEntryDateAsc(Long userId, Instant entryDate);
}
