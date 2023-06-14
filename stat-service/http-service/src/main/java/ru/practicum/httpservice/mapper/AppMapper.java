package ru.practicum.httpservice.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.httpservice.entity.AppEntity;
import ru.practicum.dto.HitDto;

@UtilityClass
public class AppMapper {
    public static AppEntity toAppEntity(HitDto dto) {
        AppEntity app = new AppEntity();

        app.setName(dto.getApp());

        return app;
    }
}