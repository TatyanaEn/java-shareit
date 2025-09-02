package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class ItemDto {
    Long id;
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
    UserDto owner;

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
