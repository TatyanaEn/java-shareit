package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Optional;
import java.util.Set;

public interface ItemStorage {

    Long createItem(Item item);

    Optional<Item> getItemById(Long itemId);

    Long updateItem(Item item);

    Set<Item> getAllItemsByOwner(Long userId);

    Set<Item> findItems(String text);
}
