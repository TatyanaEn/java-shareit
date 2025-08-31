package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@Builder
public class CommentDto {
    Long id;
    @NotBlank
    @NotNull
    @Size(max = 2000)
    String text;
    ItemWithBookingDto item;
    UserDto author;
}
