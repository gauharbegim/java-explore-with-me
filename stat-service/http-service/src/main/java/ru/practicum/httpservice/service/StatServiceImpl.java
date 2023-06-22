package ru.practicum.httpservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHit;
import ru.practicum.httpservice.entity.HitEntity;
import ru.practicum.httpservice.mapper.HitMapper;
import ru.practicum.httpservice.repository.HitRepository;
import ru.practicum.dto.ViewStats;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatServiceImpl implements StatService {
    private final HitRepository hitRepository;

    @Override
    @Transactional
    public void saveHit(EndpointHit endpointHit) {
        log.info("--------------------------saveHit start------------------------------------");
        log.info("endpointHit:  " + endpointHit);
        HitEntity hitEntity = HitMapper.toHitEntity(endpointHit);
        hitRepository.save(hitEntity);
        log.info("--------------------------saveHit end------------------------------------");
    }

    @Override
    public List<ViewStats> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Ошибка в временном промежутоке");
        }

        if (uris.isEmpty()) {
            if (unique) {
                return hitRepository.getAllStatsDistinctIp(start, end);
            } else {
                return hitRepository.getAllStats(start, end);
            }
        } else {
            if (unique) {
                return hitRepository.getStatsByUrisDistinctIp(start, end, uris);
            } else {
                return hitRepository.getStatsByUris(start, end, uris);
            }
        }
    }

}
