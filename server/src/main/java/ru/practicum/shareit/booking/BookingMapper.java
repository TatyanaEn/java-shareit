package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class BookingMapper {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            .withZone(ZoneOffset.UTC);

    public static Booking toBooking(BookingRequestDto bookingRequestDto) {
        return Booking.builder()
                .id(bookingRequestDto.getId())
                .start(LocalDateTime.parse(bookingRequestDto.getStart(), dateTimeFormatter))
                .end(LocalDateTime.parse(bookingRequestDto.getEnd(), dateTimeFormatter))
                .status(bookingRequestDto.getStatus())
                .build();
    }

    public static BookingResponseDto toBookingDto(Booking booking) {
        String startDate = dateTimeFormatter
                .format(booking.getStart());
        String endDate = dateTimeFormatter
                .format(booking.getEnd());
        return BookingResponseDto.builder()
                .id(booking.getId())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .item(ItemMapper.toItemDto(booking.getItem()))
                .start(startDate)
                .end(endDate)
                .status(booking.getStatus())
                .build();
    }
}

