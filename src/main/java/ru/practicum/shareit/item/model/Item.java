package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class Item {
    Long id;
    String name;
    String description;
    Boolean available;
    Long ownerId;
    Long requestId;
}
