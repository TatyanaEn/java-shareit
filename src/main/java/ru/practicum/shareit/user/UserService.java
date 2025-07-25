package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public List<UserDto> findAll() {
        return userStorage.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

    }

    public UserDto getUserById(Long userId) {
        return userStorage.getUserById(userId)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден с ID: " + userId, log));
    }

    public UserDto createUser(UserDto request) {
        for (UserDto item : findAll()) {
            if (item.getEmail().equals(request.getEmail()))
                throw new DuplicatedDataException("Этот емейл уже используется", log);
        }
        User user = UserMapper.toUser(request);

        Long userId = userStorage.createUser(user);

        return UserMapper.toUserDto(userStorage.getUserById(userId).get());

    }

    public UserDto updateUser(UserDto request) {
        for (User item : userStorage.findAll()) {
            if (item.getEmail().equals(request.getEmail()))
                throw new DuplicatedDataException("Этот имейл уже используется", log);

        }
        User updatedUser = userStorage.getUserById(request.getId())
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", log));
        if (updatedUser != null) {
            long updatedUserId = userStorage.updateUser(updatedUser);
            return UserMapper.toUserDto(userStorage.getUserById(updatedUserId).get());
        } else
            return null;
    }


    public void deleteUser(Long userId) {
        if (userStorage.getUserById(userId).isEmpty())
            throw new NotFoundException("Пользователь с id = " + userId + " не найден", log);

        userStorage.deleteUser(userId);

    }


}
