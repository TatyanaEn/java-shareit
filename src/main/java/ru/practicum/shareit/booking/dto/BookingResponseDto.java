package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@Builder
public class BookingResponseDto {
    Long id;

    String start;

    String end;

    ItemDto item;

    UserDto booker;

    BookingStatus status;
}
