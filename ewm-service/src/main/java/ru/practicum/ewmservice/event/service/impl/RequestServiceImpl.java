package ru.practicum.ewmservice.event.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewmservice.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewmservice.event.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.event.entity.EventEntity;
import ru.practicum.ewmservice.event.entity.RequestEntity;
import ru.practicum.ewmservice.event.enums.EventState;
import ru.practicum.ewmservice.event.enums.RequestStatus;
import ru.practicum.ewmservice.event.enums.RequestStatusAction;
import ru.practicum.ewmservice.event.mapper.RequestMapper;
import ru.practicum.ewmservice.event.repository.RequestRepository;
import ru.practicum.ewmservice.event.service.EventService;
import ru.practicum.ewmservice.event.service.RequestService;
import ru.practicum.ewmservice.event.service.StatsService;
import ru.practicum.ewmservice.exception.ForbiddenException;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.user.entity.UserEntity;
import ru.practicum.ewmservice.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final UserService userService;
    private final EventService eventService;
    private final StatsService statsService;
    private final RequestRepository requestRepository;

    @Override
    public List<ParticipationRequestDto> getEventRequestsByRequester(Long userId) {
        userService.getUserById(userId);

        return toParticipationRequestsDto(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    @Transactional
    public ParticipationRequestDto createEventRequest(Long userId, Long eventId) {
        UserEntity user = userService.getUserById(userId);
        EventEntity event = eventService.getEventById(eventId);

        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ForbiddenException("Нельзя создавать запрос на собственное событие.");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Нельзя создавать запрос на неопубликованное событие.");
        }

        Optional<RequestEntity> oldRequest = requestRepository.findByEventIdAndRequesterId(eventId, userId);

        if (oldRequest.isPresent()) {
            throw new ForbiddenException("Создавать повторный запрос запрещено.");
        }

        checkIsNewLimitGreaterOld(
                statsService.getConfirmedRequests(List.of(event)).getOrDefault(eventId, 0L) + 1,
                event.getParticipantLimit()
        );

        RequestEntity newRequest = RequestEntity.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            newRequest.setStatus(RequestStatus.CONFIRMED);
        } else {
            newRequest.setStatus(RequestStatus.PENDING);
        }

        return RequestMapper.toParticipationRequestDto(requestRepository.save(newRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelEventRequest(Long userId, Long requestId) {
        userService.getUserById(userId);

        RequestEntity request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявки на участие с таким id не существует."));

        checkUserIsOwner(request.getRequester().getId(), userId);

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getEventRequestsByEventOwner(Long userId, Long eventId) {
        userService.getUserById(userId);
        EventEntity event = eventService.getEventById(eventId);

        checkUserIsOwner(event.getInitiator().getId(), userId);

        return toParticipationRequestsDto(requestRepository.findAllByEventId(eventId));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult patchEventRequestsByEventOwner(
            Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        userService.getUserById(userId);
        EventEntity event = eventService.getEventById(eventId);

        checkUserIsOwner(event.getInitiator().getId(), userId);

        if (!event.getRequestModeration() ||
                event.getParticipantLimit() == 0 ||
                eventRequestStatusUpdateRequest.getRequestIds().isEmpty()) {
            return new EventRequestStatusUpdateResult(List.of(), List.of());
        }

        List<RequestEntity> confirmedList = new ArrayList<>();
        List<RequestEntity> rejectedList = new ArrayList<>();

        List<RequestEntity> requests = requestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        if (requests.size() != eventRequestStatusUpdateRequest.getRequestIds().size()) {
            throw new NotFoundException("Некоторые запросы на участие не найдены.");
        }

        if (!requests.stream()
                .map(RequestEntity::getStatus)
                .allMatch(RequestStatus.PENDING::equals)) {
            throw new ForbiddenException("Изменять можно только заявки, находящиеся в ожидании.");
        }

        if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatusAction.REJECTED)) {
            rejectedList.addAll(changeStatusAndSave(requests, RequestStatus.REJECTED));
        } else {
            Long newConfirmedRequests = statsService.getConfirmedRequests(List.of(event)).getOrDefault(eventId, 0L) +
                    eventRequestStatusUpdateRequest.getRequestIds().size();

            checkIsNewLimitGreaterOld(newConfirmedRequests, event.getParticipantLimit());

            confirmedList.addAll(changeStatusAndSave(requests, RequestStatus.CONFIRMED));

            if (newConfirmedRequests >= event.getParticipantLimit()) {
                rejectedList.addAll(changeStatusAndSave(
                        requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.PENDING),
                        RequestStatus.REJECTED)
                );
            }
        }

        return new EventRequestStatusUpdateResult(toParticipationRequestsDto(confirmedList),
                toParticipationRequestsDto(rejectedList));
    }

    private List<ParticipationRequestDto> toParticipationRequestsDto(List<RequestEntity> requests) {
        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    private List<RequestEntity> changeStatusAndSave(List<RequestEntity> requests, RequestStatus status) {
        requests.forEach(request -> request.setStatus(status));
        return requestRepository.saveAll(requests);
    }

    private void checkIsNewLimitGreaterOld(Long newLimit, Integer eventParticipantLimit) {
        if (eventParticipantLimit != 0 && (newLimit > eventParticipantLimit)) {
            throw new ForbiddenException(String.format("Достигнут лимит подтвержденных запросов на участие: %d",
                    eventParticipantLimit));
        }
    }

    private void checkUserIsOwner(Long id, Long userId) {
        if (!Objects.equals(id, userId)) {
            throw new ForbiddenException("Пользователь не является владельцем.");
        }
    }
}