package ru.practicum.httpservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.httpservice.entity.HitEntity;
import ru.practicum.dto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<HitEntity, Long> {
    @Query("SELECT new ru.practicum.dto.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM HitEntity AS s " +
            "WHERE s.timeStamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStats> getAllStatsDistinctIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM HitEntity AS s " +
            "WHERE s.timeStamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStats> getAllStats(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM HitEntity AS s " +
            "WHERE s.timeStamp BETWEEN ?1 AND ?2 " +
            "AND s.uri IN (?3) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    List<ViewStats> getStatsByUrisDistinctIp(LocalDateTime start, LocalDateTime end, List<String> uri);

    @Query("SELECT new ru.practicum.dto.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM HitEntity AS s " +
            "WHERE s.timeStamp BETWEEN ?1 AND ?2 " +
            "AND s.uri IN (?3) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<ViewStats> getStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uri);

}
