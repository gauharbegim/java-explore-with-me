package ru.practicum.httpservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.httpservice.entity.HitEntity;
import ru.practicum.dto.StatDto;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<HitEntity, Long> {
    @Query(name = "GetNotUniqueIpStat", nativeQuery = true)
    List<StatDto> getNotUniqueIpStat(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(name = "GetUniqueIpStat", nativeQuery = true)
    List<StatDto> getUniqueIpStat(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(name = "GetNotUniqueIpStatNoUri", nativeQuery = true)
    List<StatDto> getNotUniqueIpStatNoUri(LocalDateTime start, LocalDateTime end);

    @Query(name = "GetNotUniqueIpStatNoUri", nativeQuery = true)
    List<StatDto> getUniqueIpStatNoUri(LocalDateTime start, LocalDateTime end);
}
