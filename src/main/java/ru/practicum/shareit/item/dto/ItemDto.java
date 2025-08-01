package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    Long id;
    @NotBlank
    @NotNull
    String name;
    @NotBlank
    @NotNull
    String description;
    @NotNull
    Boolean available;
    Long ownerId;
    Long requestId;
}
