package ru.practicum.shareit.exception;

import org.slf4j.Logger;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message, Logger log) {

        super(message);
        log.error(message, this);

    }
}