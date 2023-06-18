package ru.practicum.ewmservice.event.mapper;

import ru.practicum.ewmservice.event.dto.ParticipationRequestDto;
import ru.practicum.ewmservice.event.entity.EventEntity;
import ru.practicum.ewmservice.event.entity.RequestEntity;
import ru.practicum.ewmservice.user.entity.UserEntity;

public class RequestMapper {
    public static RequestEntity toRequestEntity(ParticipationRequestDto dto) {
        return RequestEntity.builder()
                .id(dto.getId())
                .event(EventEntity.builder()
                        .id(dto.getEvent())
                        .build())
                .requester(UserEntity.builder()
                        .id(dto.getRequester())
                        .build())
                .created(dto.getCreated())
                .status(dto.getStatus())
                .build();
    }

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
