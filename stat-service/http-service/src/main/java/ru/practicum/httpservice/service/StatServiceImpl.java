package ru.practicum.httpservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHit;
import ru.practicum.httpservice.entity.AppEntity;
import ru.practicum.httpservice.entity.HitEntity;
import ru.practicum.httpservice.exception.InvalidPeriodException;
import ru.practicum.httpservice.mapper.AppMapper;
import ru.practicum.httpservice.mapper.HitMapper;
import ru.practicum.httpservice.repository.AppRepository;
import ru.practicum.httpservice.repository.HitRepository;
import ru.practicum.dto.ViewStats;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class StatServiceImpl implements StatService {
    private final AppRepository appRepo;
    private final HitRepository hitRepo;

    @Override
    public void saveHit(EndpointHit hit) {
        AppEntity app = getOrCreate(hit);
        HitEntity hitEntity = HitMapper.toHitEntity(hit, app);
        hitRepo.save(hitEntity);
    }

    @Override
    public List<ViewStats> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        checkPeriod(start, end);
        return get(start, end, uris, unique);
    }

    private AppEntity getOrCreate(EndpointHit hit) {
        return appRepo.getByName(hit.getApp())
                .orElseGet(() -> appRepo.save(AppMapper.toAppEntity(hit)));
    }

    private List<ViewStats> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris.isEmpty()) {
            if (unique) {
                return hitRepo.getAllStatsDistinctIp(start, end);
            } else {
                return hitRepo.getAllStats(start, end);
            }
        } else {
            if (unique) {
                return hitRepo.getStatsByUrisDistinctIp(start, end, uris);
            } else {
                return hitRepo.getStatsByUris(start, end, uris);
            }
        }
    }

    private void checkPeriod(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new InvalidPeriodException("Неверные параметры периода");
        }
    }

}
