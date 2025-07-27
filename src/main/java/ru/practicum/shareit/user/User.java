package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class User {
    Long id;
    String name;
    String email;
}
