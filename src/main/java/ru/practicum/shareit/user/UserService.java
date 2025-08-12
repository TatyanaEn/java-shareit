package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto getUserById(Long userId);

    UserDto createUser(UserDto request);

    UserDto updateUser(UserDto request);

    void deleteUser(Long userId);
}
