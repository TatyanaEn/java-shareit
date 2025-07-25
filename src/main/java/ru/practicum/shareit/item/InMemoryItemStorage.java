package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.utils.GenratorIds;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Qualifier("InMemoryItemStorage")
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Long, Item> items;

    @Override
    public Long createItem(Item item) {
        Item newItem = Item.builder()
                .id(GenratorIds.getNextId(items))
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwnerId())
                .requestId(item.getRequestId())
                .build();
        items.put(newItem.getId(), newItem);

        return newItem.getId();
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        Item itemFS = items.get(itemId);
        if (itemFS == null)
            return Optional.empty();
        return Optional.ofNullable(Item.builder()
                .id(itemId)
                .name(itemFS.getName())
                .description(itemFS.getDescription())
                .available(itemFS.getAvailable())
                .ownerId(itemFS.getOwnerId())
                .requestId(itemFS.getRequestId())
                .build());
    }

    @Override
    public Long updateItem(Item newItem) {
        Item oldItem = items.get(newItem.getId());
        if (!(newItem.getName() == null || newItem.getName().isBlank()))
            oldItem.setName(newItem.getName());
        if (!(newItem.getDescription() == null || newItem.getDescription().isBlank()))
            oldItem.setDescription(newItem.getDescription());
        if (newItem.getAvailable() != null)
            oldItem.setAvailable(newItem.getAvailable());
        items.put(oldItem.getId(), oldItem);
        return oldItem.getId();
    }

    @Override
    public Set<Item> getAllItemsByOwner(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .map(item -> Item.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .ownerId(item.getOwnerId())
                        .requestId(item.getRequestId())
                        .build())
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Item> findItems(String text) {
        return items.values().stream()
                .filter(item -> ((item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        item.getAvailable().equals(true)))
                .map(item -> Item.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .ownerId(item.getOwnerId())
                        .requestId(item.getRequestId())
                        .build())
                .collect(Collectors.toSet());
    }

}
