package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto request);

    ItemDto updateItem(Long userId, ItemDto request);

    ItemWithBookingDto getItemById(Long userId, Long itemId);

    List<ItemWithBookingDto> findAllItemsByOwner(Long userId);

    List<ItemDto> findItems(String text);

    CommentResponseDto createComment(CommentDto request);
}
