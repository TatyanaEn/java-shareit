package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemDto {

    @NotBlank
    @NotNull
    @Size(max = 255)
    String name;
    @NotBlank
    @NotNull
    @Size(max = 2000)
    String description;
    @NotNull
    Boolean available;

    //List<CommentDto> comments;

    Long requestId;
}
