package ru.practicum.ewmservice.user.mapper;

import ru.practicum.ewmservice.user.dto.NewUserRequest;
import ru.practicum.ewmservice.user.dto.UserDto;
import ru.practicum.ewmservice.user.entity.UserEntity;

public class UserMapper {
    public static UserDto toUserDto(UserEntity entity) {
        return UserDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .name(entity.getName())
                .build();
    }

    public static UserEntity toUserEntity(NewUserRequest dto) {
        return UserEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public static UserEntity toUserEntity(UserDto dto) {
        return UserEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }
}
