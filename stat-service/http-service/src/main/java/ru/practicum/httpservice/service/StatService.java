package ru.practicum.httpservice.service;

import ru.practicum.dto.EndpointHit;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitOutputDto;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    void saveHit(EndpointHit hit);

    List<ViewStats> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
