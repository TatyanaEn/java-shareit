package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    Long id;
    @NotBlank
    String name;
    @Email
    @NotBlank
    @NotNull
    String email;
}
