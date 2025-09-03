package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Data
@Builder
public class ItemWithBookingDto {
    Long id;
    @NotBlank
    @NotNull
    String name;
    @NotBlank
    @NotNull
    String description;
    @NotNull
    Boolean available;
    UserDto owner;
    Booking lastBooking;
    Booking nextBooking;
    List<CommentDto> comments;
    Long requestId;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasAvailable() {
        return !(available == null);
    }

    public boolean hasOwner() {
        return !(owner == null);
    }

}
