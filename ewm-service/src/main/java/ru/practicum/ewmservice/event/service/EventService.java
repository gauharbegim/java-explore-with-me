package ru.practicum.ewmservice.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewmservice.event.dto.*;
import ru.practicum.ewmservice.event.entity.EventEntity;
import ru.practicum.ewmservice.event.enums.EventSortType;
import ru.practicum.ewmservice.event.enums.EventState;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<ResultEventDto> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    ResultEventDto editEventByAdmin(Long eventId, UpdateEventDto updateEventAdminRequest);

    List<EventDto> getAllEventsByPrivate(Long userId, Pageable pageable);

    ResultEventDto createEventByPrivate(Long userId, NewEventDto newEventDto);

    ResultEventDto getEventByPrivate(Long userId, Long eventId);

    ResultEventDto editEventByPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventDto> getEventsByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd, Boolean onlyAvailable, EventSortType sort,
                                          Integer from, Integer size, HttpServletRequest request);

    ResultEventDto getEventByPublic(Long id, HttpServletRequest request);

    EventEntity getEventById(Long eventId);

    List<EventEntity> getEventsByIds(List<Long> eventsId);

    List<EventDto> toEventsShortDto(List<EventEntity> events);
}