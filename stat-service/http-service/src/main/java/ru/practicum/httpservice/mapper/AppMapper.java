package ru.practicum.httpservice.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.EndpointHit;
import ru.practicum.httpservice.entity.AppEntity;

@UtilityClass
public class AppMapper {
    public static AppEntity toAppEntity(EndpointHit dto) {
        AppEntity app = new AppEntity();

        app.setName(dto.getApp());

        return app;
    }
}