package ru.practicum.ewmservice.event.mapper;

import ru.practicum.ewmservice.event.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.event.entity.RequestEntity;

public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(RequestEntity entity) {
        return ParticipationRequestDto.builder()
                .id(entity.getId())
                .event(entity.getEvent().getId())
                .requester(entity.getRequester().getId())
                .created(entity.getCreated())
                .status(entity.getStatus())
                .build();
    }
}
