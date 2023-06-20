package ru.practicum.ewmservice.event.service;

import ru.practicum.dto.ViewStats;
import ru.practicum.ewmservice.event.entity.EventEntity;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StatsService {
    void addHit(HttpServletRequest request);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

    Map<Long, Long> getViews(List<EventEntity> events);

    Map<Long, Long> getConfirmedRequests(List<EventEntity> events);
}