package ru.practicum.ewmservice.event.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.category.entity.CategoryEntity;
import ru.practicum.ewmservice.category.mapper.CategoryMapper;
import ru.practicum.ewmservice.event.dto.EventDto;
import ru.practicum.ewmservice.event.dto.NewEventDto;
import ru.practicum.ewmservice.event.entity.EventEntity;
import ru.practicum.ewmservice.user.mapper.UserMapper;

@Component
public class EventMapper {
    public EventDto toEventDto(EventEntity entity) {
        return EventDto.builder()
                .annotation(entity.getAnnotation())
                .category(CategoryMapper.toCategoryDto(entity.getCategory()))
//                .confirmedRequests()
                .createdOn(entity.getCreatedOn())
                .description(entity.getDescription())
                .eventDate(entity.getEventDate())
                .id(entity.getId())
                .initiator(UserMapper.toUserDto(entity.getInitiator()))
                .location(LocationMapper.toLocationDto(entity.getLocation()))
                .paid(entity.getPaid())
                .participantLimit(entity.getParticipantLimit())
                .publishedOn(entity.getPublishedOn())
                .requestModeration(entity.getRequestModeration())
                .state(entity.getState())
                .title(entity.getTitle())
//                .views(entity.getV())
                .build();
    }

    public EventEntity toEventEntity(EventDto dto) {
        return EventEntity.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .annotation(dto.getAnnotation())
                .category(CategoryMapper.toCategoryEntity(dto.getCategory()))
                .description(dto.getDescription())
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .eventDate(dto.getEventDate())
                .location(LocationMapper.toLocationEntity(dto.getLocation()))
                .createdOn(dto.getCreatedOn())
                .state(dto.getState())
                .publishedOn(dto.getPublishedOn())
                .initiator(UserMapper.toUserEntity(dto.getInitiator()))
                .requestModeration(dto.getRequestModeration())
                .build();
    }

    public EventEntity toEventEntity(NewEventDto dto) {
        return EventEntity.builder()
                .id(null)
                .title(dto.getTitle())
                .annotation(dto.getAnnotation())
                .category(CategoryEntity.builder()
                        .id(dto.getCategory())
                        .build())
                .description(dto.getDescription())
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .eventDate(dto.getEventDate())
                .location(LocationMapper.toLocationEntity(dto.getLocation()))
                .createdOn(null)
                .state(null)
                .publishedOn(null)
                .initiator(null)
                .requestModeration(dto.getRequestModeration())
                .build();
    }
}
