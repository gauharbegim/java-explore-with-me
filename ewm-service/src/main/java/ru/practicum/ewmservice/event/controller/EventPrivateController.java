package ru.practicum.ewmservice.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmservice.constants.SystemConstats;
import ru.practicum.ewmservice.event.dto.*;
import ru.practicum.ewmservice.event.service.EventService;
import ru.practicum.ewmservice.event.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Validated
public class EventPrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventDto> getAllEventsByPrivate(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = SystemConstats.PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = SystemConstats.PAGE_DEFAULT_SIZE) @Positive Integer size) {
        return eventService.getAllEventsByPrivate(userId, PageRequest.of(from / size, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEventByPrivate(@PathVariable Long userId,
                                         @Valid @RequestBody EventDto newEventDto) {
        return eventService.createEventByPrivate(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto getEventByPrivate(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        return eventService.getEventByPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventDto patchEventByPrivate(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventDto updateEventUserRequest) {
        return eventService.editEventByPrivate(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequestsByEventOwner(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        return requestService.getEventRequestsByEventOwner(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult patchEventRequestsByEventOwner(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return requestService.patchEventRequestsByEventOwner(userId, eventId, eventRequestStatusUpdateRequest);
    }
}