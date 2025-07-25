package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public final class UserMapper {

    public static User toUser(UserDto userDto) {
        return User.builder()
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
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        return user;
    }

}