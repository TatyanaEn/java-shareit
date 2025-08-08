package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemDto createItem(ItemDto request) {
        userStorage.getUserById(request.getOwnerId());
        Long itemId = itemStorage.createItem(ItemMapper.toItem(request));
        return ItemMapper.toItemDto(itemStorage.getItemById(itemId).get());
    }

    public ItemDto updateItem(Long userId, ItemDto request) {

        if (!itemStorage.getItemById(request.getId()).get().getOwnerId().equals(userId)) {
            throw new NotFoundException("Редактировать вещь может только её владелец", log);
        }

        Long itemId = itemStorage.updateItem(ItemMapper.toItem(request));
        return ItemMapper.toItemDto(itemStorage.getItemById(itemId).get());
    }

    public ItemDto getItemById(Long itemId) {
        return itemStorage.getItemById(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + itemId + " не найдена", log));
    }

    public List<ItemDto> findAllItemsByOwner(Long userId) {
        return itemStorage.getAllItemsByOwner(userId).stream().map(ItemMapper::toItemDto).toList();
    }

    public List<ItemDto> findItems(String text) {
        if (text.isBlank()) {
            return List.of();
        } else {
            return itemStorage.findItems(text).stream().map(ItemMapper::toItemDto).toList();
        }
    }
}
