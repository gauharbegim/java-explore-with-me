package ru.practicum.ewmservice.compilation.mapper;

import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.dto.NewCompilationDto;
import ru.practicum.ewmservice.compilation.entity.CompilationEntity;

public class CompilationMapper {
    public static CompilationEntity toCompilationEntity(NewCompilationDto newCompilationDto) {
        return CompilationEntity.builder()
                .id(null)
//                TODO clean
//                .events(newCompilationDto.getEvents())
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }

    public static CompilationDto toCompilationDto(CompilationEntity entity) {
        return CompilationDto.builder()

                .build();
    }
}
