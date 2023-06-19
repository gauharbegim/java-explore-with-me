package ru.practicum.ewmservice.event.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.category.entity.CategoryEntity;
import ru.practicum.ewmservice.category.mapper.CategoryMapper;
import ru.practicum.ewmservice.event.dto.NewEventDto;
import ru.practicum.ewmservice.event.dto.EventFullDto;
import ru.practicum.ewmservice.event.entity.EventEntity;
import ru.practicum.ewmservice.event.entity.LocationEntity;
import ru.practicum.ewmservice.event.enums.EventState;
import ru.practicum.ewmservice.user.entity.UserEntity;
import ru.practicum.ewmservice.user.mapper.UserMapper;

import java.time.LocalDateTime;

@Component
public class EventMapper {
    public static EventFullDto toEventFullDto(EventEntity entity, Long confirmedRequests, Long view) {
        return EventFullDto.builder()
                .annotation(entity.getAnnotation())
                .category(CategoryMapper.toCategoryDto(entity.getCategory()))
                .confirmedRequests(confirmedRequests)
                .createdOn(entity.getCreatedOn())
                .description(entity.getDescription())
                .eventDate(entity.getEventDate())
                .id(entity.getId())
                .initiator(UserMapper.toUserShortDto(entity.getInitiator()))
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