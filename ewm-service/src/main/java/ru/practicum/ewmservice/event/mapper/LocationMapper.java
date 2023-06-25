package ru.practicum.ewmservice.event.mapper;

import ru.practicum.ewmservice.event.dto.LocationDto;
import ru.practicum.ewmservice.event.entity.LocationEntity;

public class LocationMapper {
    public static LocationEntity toLocationEntity(LocationDto dto) {
        return LocationEntity.builder()
                .id(null)
                .lat(dto.getLat())
                .lon(dto.getLon())
                .build();
    }

    public static LocationDto toLocationDto(LocationEntity entity) {
        return LocationDto.builder()
                .lat(entity.getLat())
                .lon(entity.getLon())
                .build();
    }
}
