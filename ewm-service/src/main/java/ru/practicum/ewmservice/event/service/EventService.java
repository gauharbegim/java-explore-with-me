package ru.practicum.ewmservice.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewmservice.event.dto.EventDto;
import ru.practicum.ewmservice.event.dto.UpdateEventDto;
import ru.practicum.ewmservice.event.entity.EventEntity;
import ru.practicum.ewmservice.event.enums.EventSortType;
import ru.practicum.ewmservice.event.enums.EventState;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    List<EventDto> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventDto editEventByAdmin(Long eventId, UpdateEventDto updateEventAdminRequest);

    List<EventDto> getAllEventsByPrivate(Long userId, Pageable pageable);

    EventDto createEventByPrivate(Long userId, EventDto newEventDto);

    EventDto getEventByPrivate(Long userId, Long eventId);

    EventDto editEventByPrivate(Long userId, Long eventId, UpdateEventDto updateEventUserRequest);

    List<EventDto> getEventsByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd, Boolean onlyAvailable, EventSortType sort,
                                          Integer from, Integer size, HttpServletRequest request);

    EventDto getEventByPublic(Long id, HttpServletRequest request);

    EventEntity getEventById(Long eventId);

    List<EventEntity> getEventsByIds(List<Long> eventsId);

    List<EventDto> toEventsShortDto(List<EventEntity> events);
}