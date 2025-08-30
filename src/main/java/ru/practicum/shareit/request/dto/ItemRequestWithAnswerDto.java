package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestWithAnswerDto {
        Long id;

        String description;

        UserDto requestor;

        LocalDateTime created;

        List<Item>  items;
}
