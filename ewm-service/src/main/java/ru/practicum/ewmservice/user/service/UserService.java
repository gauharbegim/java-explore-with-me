package ru.practicum.ewmservice.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewmservice.user.dto.UserDto;
import ru.practicum.ewmservice.user.entity.UserEntity;

import java.util.List;

public interface UserService {
    UserDto create(UserDto newUserDto);

    List<UserDto> getUsers(List<Long> ids, Pageable pageable);

    void deleteById(Long id);

    UserEntity getUserById(Long id);
}