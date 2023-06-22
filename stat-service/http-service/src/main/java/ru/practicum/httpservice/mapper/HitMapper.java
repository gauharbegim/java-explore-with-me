package ru.practicum.httpservice.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.EndpointHit;
import ru.practicum.httpservice.entity.HitEntity;

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
}