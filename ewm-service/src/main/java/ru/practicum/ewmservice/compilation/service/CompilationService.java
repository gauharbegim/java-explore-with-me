package ru.practicum.ewmservice.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto create(NewCompilationDto newCompilationDto);

    CompilationDto edit(Long compId, NewCompilationDto updateCompilationRequest);

    void deleteById(Long compId);

    List<CompilationDto> getAll(Boolean pinned, Pageable pageable);

    CompilationDto getById(Long compId);
}