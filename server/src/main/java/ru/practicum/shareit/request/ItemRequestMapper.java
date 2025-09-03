package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;
import ru.practicum.shareit.user.UserMapper;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {

        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .requestor(UserMapper.toUser(itemRequestDto.getRequestor()))
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestor(UserMapper.toUserDto(itemRequest.getRequestor()))
                .build();

    }

    public static ItemRequestWithAnswerDto toItemRequestWithAnswerDto(ItemRequest itemRequest) {
        return ItemRequestWithAnswerDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requestor(UserMapper.toUserDto(itemRequest.getRequestor()))
                .build();

    }
}
