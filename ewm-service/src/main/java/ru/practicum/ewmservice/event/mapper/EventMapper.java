package ru.practicum.ewmservice.event.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.category.entity.CategoryEntity;
import ru.practicum.ewmservice.category.mapper.CategoryMapper;
import ru.practicum.ewmservice.event.dto.EventDto;
import ru.practicum.ewmservice.event.dto.NewEventDto;
import ru.practicum.ewmservice.event.dto.ResultEventDto;
import ru.practicum.ewmservice.event.entity.EventEntity;
import ru.practicum.ewmservice.event.entity.LocationEntity;
import ru.practicum.ewmservice.event.enums.EventState;
import ru.practicum.ewmservice.user.entity.UserEntity;
import ru.practicum.ewmservice.user.mapper.UserMapper;

import java.time.LocalDateTime;

@Component
public class EventMapper {
    public static EventDto toEventDto(EventEntity entity) {
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

    public static ResultEventDto toResultEventDto(EventEntity entity, Long confirmedRequests, Long view) {
        return ResultEventDto.builder()
                .annotation(entity.getAnnotation())
                .category(CategoryMapper.toCategoryDto(entity.getCategory()))
                .confirmedRequests(confirmedRequests)
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
                .views(view)
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

    public static EventEntity toEventEntity(NewEventDto dto, CategoryEntity category, LocationEntity location, UserEntity initiator) {
        return EventEntity.builder()
                .id(null)
                .title(dto.getTitle())
                .annotation(dto.getAnnotation())
                .category(category)
                .description(dto.getDescription())
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .eventDate(dto.getEventDate())
                .location(location)
                .createdOn(LocalDateTime.now())
                .state(EventState.PENDING)
                .publishedOn(null)
                .initiator(initiator)
                .requestModeration(dto.getRequestModeration())
                .build();
    }
}
