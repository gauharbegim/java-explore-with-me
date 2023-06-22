package ru.practicum.ewmservice.event.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.category.entity.CategoryEntity;
import ru.practicum.ewmservice.category.service.CategoryService;
import ru.practicum.ewmservice.event.dto.*;
import ru.practicum.ewmservice.event.entity.EventEntity;
import ru.practicum.ewmservice.event.entity.LocationEntity;
import ru.practicum.ewmservice.event.enums.EventSortType;
import ru.practicum.ewmservice.event.enums.EventState;
import ru.practicum.ewmservice.event.mapper.EventMapper;
import ru.practicum.ewmservice.event.mapper.LocationMapper;
import ru.practicum.ewmservice.event.repository.EventRepository;
import ru.practicum.ewmservice.event.repository.LocationRepository;
import ru.practicum.ewmservice.event.service.EventService;
import ru.practicum.ewmservice.event.service.StatsService;
import ru.practicum.ewmservice.exception.ForbiddenException;
import ru.practicum.ewmservice.exception.InvalidParametrException;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.user.entity.UserEntity;
import ru.practicum.ewmservice.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventServiceImpl implements EventService {
    private final UserService userService;
    private final CategoryService categoryService;
    private final StatsService statsService;
    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        checkStartIsBeforeEnd(rangeStart, rangeEnd);

        List<EventEntity> events = eventRepository.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);

        return toEventsFullDto(events);
    }

    @Override
    public EventFullDto editEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Обновление события с id {} по запросу администратора с параметрами {}", eventId, updateEventAdminRequest);

        checkNewEventDate(updateEventAdminRequest.getEventDate(), LocalDateTime.now().plusHours(1));

        EventEntity event = getEventById(eventId);

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }

        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(categoryService.getCategoryById(updateEventAdminRequest.getCategory()));
        }

        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }

        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }

        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(getOrSaveLocation(updateEventAdminRequest.getLocation()));
        }

        if (updateEventAdminRequest.getParticipantLimit() != null) {
            checkIsNewLimitNotLessOld(updateEventAdminRequest.getParticipantLimit(),
                    statsService.getConfirmedRequests(List.of(event)).getOrDefault(eventId, 0L));

            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (!event.getState().equals(EventState.PENDING)) {
                throw new ForbiddenException(String.format("Field: stateAction. Error: опубликовать можно только " +
                        "события, находящиеся в ожидании публикации. Текущий статус: %s", event.getState()));
            }

            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.REJECTED);
                    break;
            }
        }

        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        return toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getAllEventsByPrivate(Long userId, Pageable pageable) {
        userService.getUserById(userId);

        List<EventEntity> events = eventRepository.findAllByInitiatorId(userId, pageable);

        return toEventsShortDto(events);
    }

    @Override
    public EventFullDto createEventByPrivate(Long userId, NewEventDto newEventDto) {
        checkNewEventDate(newEventDto.getEventDate(), LocalDateTime.now().plusHours(2));

        UserEntity eventUser = userService.getUserById(userId);
        CategoryEntity eventCategory = categoryService.getCategoryById(newEventDto.getCategory());
        LocationEntity eventLocation = getOrSaveLocation(newEventDto.getLocation());

        EventEntity newEventEntity = EventMapper.toEventEntity(newEventDto, eventCategory, eventLocation, eventUser);

        return toEventFullDto(eventRepository.save(newEventEntity));
    }

    @Override
    public EventFullDto getEventByPrivate(Long userId, Long eventId) {
        log.info("Вывод события с id {}, созданного пользователем с id {}", eventId, userId);

        userService.getUserById(userId);

        EventEntity event = getEventByIdAndInitiatorId(eventId, userId);

        return toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto editEventByPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.info("Обновление события с id {} по запросу пользователя с id {} с новыми параметрами {}",
                eventId, userId, updateEventUserRequest);

        checkNewEventDate(updateEventUserRequest.getEventDate(), LocalDateTime.now().plusHours(2));

        userService.getUserById(userId);

        EventEntity event = getEventByIdAndInitiatorId(eventId, userId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Изменять можно только неопубликованные или отмененные события.");
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }

        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(categoryService.getCategoryById(updateEventUserRequest.getCategory()));
        }

        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }

        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }

        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(getOrSaveLocation(updateEventUserRequest.getLocation()));
        }

        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }

        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        return toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEventsByPublic(
            String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
            Boolean onlyAvailable, EventSortType sort, Integer from, Integer size, HttpServletRequest request) {
        checkStartIsBeforeEnd(rangeStart, rangeEnd);

        List<EventEntity> events = eventRepository.getEventsByPublic(text, categories, paid, rangeStart, rangeEnd, from, size);

        if (events.isEmpty()) {
            return List.of();
        }

        Map<Long, Integer> eventsParticipantLimit = new HashMap<>();
        events.forEach(event -> eventsParticipantLimit.put(event.getId(), event.getParticipantLimit()));

        List<EventShortDto> eventsShortDto = toEventsShortDto(events);

        if (onlyAvailable) {
            eventsShortDto = eventsShortDto.stream()
                    .filter(eventShort -> (eventsParticipantLimit.get(eventShort.getId()) == 0 ||
                            eventsParticipantLimit.get(eventShort.getId()) > eventShort.getConfirmedRequests()))
                    .collect(Collectors.toList());
        }

        if (needSort(sort, EventSortType.VIEWS)) {
            eventsShortDto.sort(Comparator.comparing(EventShortDto::getViews));
        } else if (needSort(sort, EventSortType.EVENT_DATE)) {
            eventsShortDto.sort(Comparator.comparing(EventShortDto::getEventDate));
        }

        statsService.addHit(request);

        return eventsShortDto;
    }

    @Override
    public EventFullDto getEventByPublic(Long eventId, HttpServletRequest request) {
        log.info("----> getEventByPublic(Long eventId, HttpServletRequest request)");
        EventEntity event = getEventById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие с таким id не опубликовано.");
        }

        statsService.addHit(request);

        return toEventFullDto(event);
    }

    @Override
    public EventEntity getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с таким id не существует."));
    }

    @Override
    public List<EventEntity> getEventsByIds(List<Long> eventsId) {
        log.info("Вывод списка событий с ids {}", eventsId);

        if (eventsId.isEmpty()) {
            return new ArrayList<>();
        }

        return eventRepository.findAllByIdIn(eventsId);
    }

    @Override
    public List<EventShortDto> toEventsShortDto(List<EventEntity> events) {
        log.info("Преобразование в EventShortDto списка событий events {}", events);

        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(events);

        return events.stream()
                .map((event) -> EventMapper.toEventShortDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private List<EventFullDto> toEventsFullDto(List<EventEntity> events) {
        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(events);

        return events.stream()
                .map((event) -> EventMapper.toEventFullDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private EventFullDto toEventFullDto(EventEntity event) {
        return toEventsFullDto(List.of(event)).get(0);
    }

    private EventEntity getEventByIdAndInitiatorId(Long eventId, Long userId) {
        log.info("Вывод события с id {}", eventId);

        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("События с таким id не существует."));
    }

    private LocationEntity getOrSaveLocation(LocationDto locationDto) {
        LocationEntity newLocation = LocationMapper.toLocationEntity(locationDto);
        return locationRepository.findByLatAndLon(newLocation.getLat(), newLocation.getLon())
                .orElseGet(() -> locationRepository.save(newLocation));
    }

    private Boolean needSort(EventSortType sort, EventSortType typeToCompare) {
        return sort != null && sort.equals(typeToCompare);
    }

    private void checkStartIsBeforeEnd(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new InvalidParametrException(String.format("Field: eventDate. Error: некорректные параметры временного " +
                    "интервала. Value: rangeStart = %s, rangeEnd = %s", rangeStart, rangeEnd));
        }
    }

    private void checkNewEventDate(LocalDateTime newEventDate, LocalDateTime minTimeBeforeEventStart) {
        if (newEventDate != null && newEventDate.isBefore(minTimeBeforeEventStart)) {
            throw new InvalidParametrException(String.format("Field: eventDate. Error: остается слишком мало времени для " +
                    "подготовки. Value: %s", newEventDate));
        }
    }

    private void checkIsNewLimitNotLessOld(Integer newLimit, Long eventParticipantLimit) {
        if (newLimit != 0 && eventParticipantLimit != 0 && (newLimit < eventParticipantLimit)) {
            throw new ForbiddenException(String.format("Field: stateAction. Error: Новый лимит участников должен " +
                    "быть не меньше количества уже одобренных заявок: %s", eventParticipantLimit));
        }
    }
}