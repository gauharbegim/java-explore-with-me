package ru.practicum.ewmservice.event.repository;

import ru.practicum.ewmservice.event.entity.EventEntity;
import ru.practicum.ewmservice.event.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventCriteriaRepository {
    List<EventEntity> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    List<EventEntity> getEventsByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Integer from, Integer size);
}
