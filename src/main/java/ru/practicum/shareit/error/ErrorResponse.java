package ru.practicum.shareit.error;

import lombok.Getter;

@Getter
public class ErrorResponse {
    // название ошибки
    String error;
    // подробное описание

    public ErrorResponse(String error) {
        this.error = error;
    }

}
