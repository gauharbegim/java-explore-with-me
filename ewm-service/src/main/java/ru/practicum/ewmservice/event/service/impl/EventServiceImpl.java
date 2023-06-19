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
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.user.entity.UserEntity;
import ru.practicum.ewmservice.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
@Transactional(readOnly = true)
@Slf4j
public class EventServiceImpl implements EventService {
    @PersistenceContext
    private EntityManager entityManager;
    private final UserService userService;
    private final CategoryService categoryService;
    private final StatsService statsService;
    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ResultEventDto> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        checkStartIsBeforeEnd(rangeStart, rangeEnd);

        List<EventEntity> events = getEventsByAdminCriteria(users, states, categories, rangeStart, rangeEnd, from, size);

        return toResultEventsDto(events);
    }

    public List<EventEntity> getEventsByAdminCriteria(List<Long> users, List<EventState> states, List<Long> categories,
                                                      LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        log.info("users:" + users);
        log.info("states:" + states);
        log.info("categories:" + categories);
        log.info("rangeEnd:" + rangeEnd);
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventEntity> query = builder.createQuery(EventEntity.class);
        Root<EventEntity> root = query.from(EventEntity.class);
        Predicate criteria = builder.conjunction();

        if (users != null && !users.isEmpty()) {
            log.info("***1");
            criteria = builder.and(criteria, root.get("initiator").in(users));
        }

        if (states != null && !states.isEmpty()) {
            log.info("***2");
            criteria = builder.and(criteria, root.get("state").in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            log.info("***3");
            criteria = builder.and(criteria, root.get("category").in(categories));
        }

        if (rangeStart != null) {
            log.info("***4");
            criteria = builder.and(criteria, builder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (rangeEnd != null) {
            log.info("***5");
            criteria = builder.and(criteria, builder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        query.select(root).where(criteria);
        return entityManager.createQuery(query).setFirstResult(from).setMaxResults(size).getResultList();
    }

    @Override
    @Transactional
    public ResultEventDto editEventByAdmin(Long eventId, UpdateEventDto updateEventAdminRequest) {
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

        EventEntity eventEntity = eventRepository.save(event);
        log.info("---- eventEntity: " + eventEntity);
        return toEventDto(eventEntity);
    }

    @Override
    public List<ResultEventDto> getAllEventsByPrivate(Long userId, Pageable pageable) {

        userService.getUserById(userId);

        List<EventEntity> events = eventRepository.findAllByInitiatorId(userId, pageable);

        return toEventsShortDto(events);
    }

    @Override
    @Transactional
    public ResultEventDto createEventByPrivate(Long userId, NewEventDto newEventDto) {
        checkNewEventDate(newEventDto.getEventDate(), LocalDateTime.now().plusHours(2));

        UserEntity eventUser = userService.getUserById(userId);
        CategoryEntity eventCategory = categoryService.getCategoryById(newEventDto.getCategory());
        LocationEntity eventLocation = getOrSaveLocation(newEventDto.getLocation());
        EventEntity newEvent = EventMapper.toEventEntity(newEventDto, eventCategory, eventLocation, eventUser);
        return toEventDto(eventRepository.save(newEvent));
    }

    @Override
    public ResultEventDto getEventByPrivate(Long userId, Long eventId) {
        log.info("Вывод события с id {}, созданного пользователем с id {}", eventId, userId);

        userService.getUserById(userId);

        EventEntity event = getEventByIdAndInitiatorId(eventId, userId);

        return toEventDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public ResultEventDto editEventByPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        if (updateEventUserRequest == null) {
            throw new ForbiddenException("Error body");
        }
        checkNewEventDate(updateEventUserRequest.getEventDate(), LocalDateTime.now().plusHours(2));

        userService.getUserById(userId);

        EventEntity event = getEventByIdAndInitiatorId(eventId, userId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Событие уже опубликовано");
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

        return toEventDto(eventRepository.save(event));
    }

    @Override
    public List<ResultEventDto> getEventsByPublic(
            String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
            Boolean onlyAvailable, EventSortType sort, Integer from, Integer size, HttpServletRequest request) {

        checkStartIsBeforeEnd(rangeStart, rangeEnd);

        List<EventEntity> events = getEventsByPublicCriteria(text, categories, paid, rangeStart, rangeEnd, from, size);

        if (events.isEmpty()) {
            return List.of();
        }

        Map<Long, Integer> eventsParticipantLimit = new HashMap<>();
        events.forEach(event -> eventsParticipantLimit.put(event.getId(), event.getParticipantLimit()));

        List<ResultEventDto> eventsShortDto = toEventsShortDto(events);

        if (onlyAvailable) {
            eventsShortDto = eventsShortDto.stream()
                    .filter(eventShort -> (eventsParticipantLimit.get(eventShort.getId()) == 0 ||
                            eventsParticipantLimit.get(eventShort.getId()) > eventShort.getConfirmedRequests()))
                    .collect(Collectors.toList());
        }

        if (needSort(sort, EventSortType.VIEWS)) {
            eventsShortDto.sort(Comparator.comparing(ResultEventDto::getViews));
        } else if (needSort(sort, EventSortType.EVENT_DATE)) {
            eventsShortDto.sort(Comparator.comparing(ResultEventDto::getEventDate));
        }

        statsService.addHit(request);

        return eventsShortDto;
    }

    public List<EventEntity> getEventsByPublicCriteria(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                                       LocalDateTime rangeEnd, Integer from, Integer size) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventEntity> query = builder.createQuery(EventEntity.class);
        Root<EventEntity> root = query.from(EventEntity.class);
        Predicate criteria = builder.conjunction();

        if (text != null && !text.isBlank()) {
            Predicate annotation = builder.like(builder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%");
            Predicate description = builder.like(builder.lower(root.get("description")), "%" + text.toLowerCase() + "%");
            criteria = builder.and(criteria, builder.or(annotation, description));
        }

        if (categories != null && !categories.isEmpty()) {
            criteria = builder.and(criteria, root.get("category").in(categories));
        }

        if (paid != null) {
            criteria = builder.and(criteria, root.get("paid").in(paid));
        }

        if (rangeStart == null && rangeEnd == null) {
            criteria = builder.and(criteria, builder.greaterThanOrEqualTo(root.get("eventDate"), LocalDateTime.now()));
        } else {
            if (rangeStart != null) {
                criteria = builder.and(criteria, builder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
            }

            if (rangeEnd != null) {
                criteria = builder.and(criteria, builder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            }
        }

        criteria = builder.and(criteria, root.get("state").in(EventState.PUBLISHED));

        query.select(root).where(criteria);
        return entityManager.createQuery(query).setFirstResult(from).setMaxResults(size).getResultList();
    }

    @Override
    public ResultEventDto getEventByPublic(Long eventId, HttpServletRequest request) {
        EventEntity event = getEventById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие с таким id не опубликовано.");
        }

        statsService.addHit(request);

        return toEventDto(event);
    }

    @Override
    public EventEntity getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с таким id не существует."));
    }

    @Override
    public List<EventEntity> getEventsByIds(List<Long> eventsId) {
        if (eventsId.isEmpty()) {
            return new ArrayList<>();
        }

        return eventRepository.findAllByIdIn(eventsId);
    }

    @Override
    public List<ResultEventDto> toEventsShortDto(List<EventEntity> events) {
        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(events);

        return events.stream()
                .map(event -> EventMapper.toResultEventDto(event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private List<ResultEventDto> toResultEventsDto(List<EventEntity> events) {
        Map<Long, Long> views = statsService.getViews(events);
        Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(events);

        return events.stream()
                .map(event -> EventMapper.toResultEventDto(event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private ResultEventDto toEventDto(EventEntity event) {
        return toResultEventsDto(List.of(event)).get(0);
    }

    private EventEntity getEventByIdAndInitiatorId(Long eventId, Long userId) {
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
            throw new ForbiddenException(String.format("Field: eventDate. Error: некорректные параметры временного " +
                    "интервала. Value: rangeStart = %s, rangeEnd = %s", rangeStart, rangeEnd));
        }
    }

    private void checkNewEventDate(LocalDateTime newEventDate, LocalDateTime minTimeBeforeEventStart) {
        if (newEventDate != null && newEventDate.isBefore(minTimeBeforeEventStart)) {
            throw new ForbiddenException("Error eventDate");
        }
    }

    private void checkIsNewLimitNotLessOld(Integer newLimit, Long eventParticipantLimit) {
        if (newLimit != 0 && eventParticipantLimit != 0 && (newLimit < eventParticipantLimit)) {
            throw new ForbiddenException(String.format("Field: stateAction. Error: Новый лимит участников должен " +
                    "быть не меньше количества уже одобренных заявок: %s", eventParticipantLimit));
        }
    }
}