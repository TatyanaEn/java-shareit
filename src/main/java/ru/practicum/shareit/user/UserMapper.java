package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

@Component
@RequiredArgsConstructor
public final class UserMapper {

    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        return userDto;
    }

    public static User updateUserFields(User user, UserDto userDto) {
        if (userDto.hasEmail())
            user.setEmail(userDto.getEmail());
        if (userDto.hasName())
            user.setName(userDto.getName());
        return user;
    }

}