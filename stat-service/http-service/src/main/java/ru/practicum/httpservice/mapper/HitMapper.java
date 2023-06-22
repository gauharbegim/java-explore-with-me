package ru.practicum.httpservice.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.EndpointHit;
import ru.practicum.httpservice.entity.HitEntity;
import ru.practicum.dto.HitOutputDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class HitMapper {
    public static HitEntity toHitEntity(EndpointHit dto) {
        HitEntity hit = new HitEntity();
        hit.setApp(dto.getApp());
        hit.setUri(dto.getUri());
        hit.setIp(dto.getIp());
        hit.setTimeStamp(LocalDateTime.parse(dto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return hit;
    }

    public static HitOutputDto toOutputDto(HitEntity hit) {
        return HitOutputDto.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timeStamp(hit.getTimeStamp())
                .build();
    }
}