package ru.practicum.ewmservice.compilation.mapper;

import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.dto.NewCompilationDto;
import ru.practicum.ewmservice.compilation.entity.CompilationEntity;
import ru.practicum.ewmservice.event.dto.EventShortDto;
import ru.practicum.ewmservice.event.entity.EventEntity;

import java.util.List;

public class CompilationMapper {
    public static CompilationEntity toCompilationEntity(NewCompilationDto newCompilationDto, List<EventEntity> events) {
        return CompilationEntity.builder()
                .id(null)
                .events(events)
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }

    public static CompilationDto toCompilationDto(CompilationEntity entity, List<EventShortDto> events) {
        return CompilationDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .pinned(entity.getPinned())
                .events(events)
                .build();
    }
}
