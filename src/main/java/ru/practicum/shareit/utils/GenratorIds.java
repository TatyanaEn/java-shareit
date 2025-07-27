package ru.practicum.shareit.utils;

import java.util.Map;

public class GenratorIds {

    public static <T> Long getNextId(Map<Long, T> map) {
        long currentMaxId = map.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
