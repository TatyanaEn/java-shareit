package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

    }

    @Override
    public UserDto getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID '%d' не найден. ".formatted(userId), log));
    }

    @Override
    public UserDto createUser(UserDto request) {
        User user = UserMapper.toUser(request);
        return UserMapper.toUserDto(userRepository.save(user));

    }

    @Override
    public UserDto updateUser(UserDto request) {
        Long userId = request.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID '%d' не найден. ".formatted(userId), log));

        return UserMapper.toUserDto(userRepository.save(UserMapper.updateUserFields(user, request)));
    }


    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID '%d' не найден. ".formatted(userId), log));
        userRepository.delete(user);
    }


}
