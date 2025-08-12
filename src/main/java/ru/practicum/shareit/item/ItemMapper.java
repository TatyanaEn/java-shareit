package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;

@Component
@RequiredArgsConstructor
public final class ItemMapper {

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(UserMapper.toUser(itemDto.getOwner()))
                .build();

    }

    public static Item toItem(ItemWithBookingDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(UserMapper.toUser(itemDto.getOwner()))
                .build();

    }

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(UserMapper.toUserDto(item.getOwner()));
        return itemDto;
    }

    public static ItemWithBookingDto toItemWithDatesDto(Item item) {
        ItemWithBookingDto itemDto = new ItemWithBookingDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(UserMapper.toUserDto(item.getOwner()));
        return itemDto;
    }

    public static Item updateItemFields(Item item, ItemDto itemDto) {
        if (itemDto.hasName())
            item.setName(itemDto.getName());
        if (itemDto.hasDescription())
            item.setDescription(itemDto.getDescription());
        if (itemDto.hasAvailable())
            item.setAvailable(itemDto.getAvailable());
        if (itemDto.hasOwner())
            item.setOwner(UserMapper.toUser(itemDto.getOwner()));
        return item;
    }

}
