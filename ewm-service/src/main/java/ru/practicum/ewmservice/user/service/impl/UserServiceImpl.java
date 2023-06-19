package ru.practicum.ewmservice.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.exception.NotFoundException;
import ru.practicum.ewmservice.user.dto.NewUserRequest;
import ru.practicum.ewmservice.user.dto.UserDto;
import ru.practicum.ewmservice.user.entity.UserEntity;
import ru.practicum.ewmservice.user.mapper.UserMapper;
import ru.practicum.ewmservice.user.repository.UserRepository;
import ru.practicum.ewmservice.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(NewUserRequest newUser) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUserEntity(newUser)));
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        log.info("Вывод пользователей с id {} и пагинацией {}", ids, pageable);

        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAllByIdIn(ids, pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Удаление пользователя с id {}", id);

        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден."));

        userRepository.deleteById(id);
    }

    @Override
    public UserEntity getUserById(Long id) {
        log.info("Вывод пользователя с id {}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с таким id не найден."));
    }
}