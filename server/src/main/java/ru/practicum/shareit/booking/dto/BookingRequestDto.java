package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

@Data
@Builder
public class BookingRequestDto {
    Long id;

    String start;

    String end;

    Long itemId;

    Long bookerId;

    BookingStatus status;

}
