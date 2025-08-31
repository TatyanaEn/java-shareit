package ru.practicum.shareit.exception;

import org.slf4j.Logger;

public class ValidationException extends RuntimeException {
    public ValidationException(String message, Logger log) {

        super(message);
        log.error(message, this);
    }
}
