package ru.practicum.httpservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.EndpointHit;
import ru.practicum.httpservice.entity.HitEntity;
import ru.practicum.httpservice.mapper.HitMapper;
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
    private final HitRepository hitRepository;

    @Override
    @Transactional
    public void saveHit(EndpointHit endpointHit) {
            HitEntity hitEntity = HitMapper.toHitEntity(endpointHit);
            hitRepository.save(hitEntity);
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
