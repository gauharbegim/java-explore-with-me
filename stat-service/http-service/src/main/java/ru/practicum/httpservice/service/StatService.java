package ru.practicum.httpservice.service;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitOutputDto;
import ru.practicum.dto.StatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    HitOutputDto saveHit(HitDto dto);

    List<StatDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

}
