package ru.practicum.httpservice.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.httpservice.entity.AppEntity;
import ru.practicum.httpservice.entity.HitEntity;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.HitOutputDto;

@UtilityClass
public class HitMapper {
    public static HitEntity toHitEntity(HitDto dto, AppEntity app) {
        HitEntity hit = new HitEntity();

        hit.setApp(app);
        hit.setUri(dto.getUri());
        hit.setIp(dto.getIp());
        hit.setTimeStamp(dto.getTimeStamp());

        return hit;
    }

    public static HitOutputDto toOutputDto(HitEntity hit) {
        return HitOutputDto.builder()
                .id(hit.getId())
                .app(hit.getApp().getName())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timeStamp(hit.getTimeStamp())
                .build();
    }
}