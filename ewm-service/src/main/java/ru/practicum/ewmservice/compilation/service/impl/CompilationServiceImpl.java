package ru.practicum.ewmservice.compilation.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.compilation.dto.CompilationDto;
import ru.practicum.ewmservice.compilation.dto.NewCompilationDto;
import ru.practicum.ewmservice.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewmservice.compilation.entity.CompilationEntity;
import ru.practicum.ewmservice.compilation.mapper.CompilationMapper;
import ru.practicum.ewmservice.compilation.repository.CompilationRepository;
import ru.practicum.ewmservice.compilation.service.CompilationService;
import ru.practicum.ewmservice.event.dto.EventShortDto;
import ru.practicum.ewmservice.event.entity.EventEntity;
import ru.practicum.ewmservice.event.service.EventService;
import ru.practicum.ewmservice.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final EventService eventService;
    private final CompilationRepository compilationRepository;

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        List<EventEntity> events = new ArrayList<>();

        if (!newCompilationDto.getEvents().isEmpty()) {
            events = eventService.getEventsByIds(newCompilationDto.getEvents());
            checkSize(events, newCompilationDto.getEvents());
        }

        CompilationEntity compilation = compilationRepository.save(CompilationMapper.toCompilationEntity(newCompilationDto, events));

        return getById(compilation.getId());
    }

    @Override
    @Transactional
    public CompilationDto edit(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("Обновление подборки событий с id {} и новыми параметрами {}", compId, updateCompilationRequest);

        CompilationEntity compilation = getCompilationById(compId);

        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }

        if (updateCompilationRequest.getEvents() != null) {
            List<EventEntity> events = eventService.getEventsByIds(updateCompilationRequest.getEvents());

            checkSize(events, updateCompilationRequest.getEvents());

            compilation.setEvents(events);
        }

        compilationRepository.save(compilation);

        return getById(compId);
    }

    @Override
    @Transactional
    public void deleteById(Long compId) {
        getCompilationById(compId);

        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Pageable pageable) {
        log.info("Вывод всех подборок событий с параметрами pinned = {}, pageable = {}", pinned, pageable);

        List<CompilationEntity> compilations;

        if (pinned == null) {
            compilations = compilationRepository.findAll(pageable).toList();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
        }

        Set<EventEntity> uniqueEvents = new HashSet<>();
        compilations.forEach(compilation -> uniqueEvents.addAll(compilation.getEvents()));

        Map<Long, EventShortDto> eventsShortDto = new HashMap<>();
        eventService.toEventsShortDto(new ArrayList<>(uniqueEvents))
                .forEach(event -> eventsShortDto.put(event.getId(), event));

        List<CompilationDto> result = new ArrayList<>();
        compilations.forEach(compilation -> {
            List<EventShortDto> compEventsShortDto = new ArrayList<>();
            compilation.getEvents()
                    .forEach(event -> compEventsShortDto.add(eventsShortDto.get(event.getId())));
            result.add(CompilationMapper.toCompilationDto(compilation, compEventsShortDto));
        });

        return result;
    }

    @Override
    public CompilationDto getById(Long compId) {
        CompilationEntity compilation = getCompilationById(compId);

        List<EventShortDto> eventsShortDto = eventService.toEventsShortDto(compilation.getEvents());

        return CompilationMapper.toCompilationDto(compilation, eventsShortDto);
    }

    private CompilationEntity getCompilationById(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборки с таким id не существует."));
    }

    private void checkSize(List<EventEntity> events, List<Long> eventsIdToUpdate) {
        if (events.size() != eventsIdToUpdate.size()) {
            throw new NotFoundException("Некоторые события не найдены.");
        }
    }
}