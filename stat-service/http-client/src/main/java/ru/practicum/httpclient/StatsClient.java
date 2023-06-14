package ru.practicum.httpclient;

import org.springframework.http.ResponseEntity;
import ru.practicum.dto.HitDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    void saveHit(HitDto hit);

    ResponseEntity<Object> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
