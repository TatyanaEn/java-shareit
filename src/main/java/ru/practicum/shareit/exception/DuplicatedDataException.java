package ru.practicum.shareit.exception;

import org.slf4j.Logger;

public class DuplicatedDataException extends RuntimeException {
    public DuplicatedDataException(String message, Logger log) {
        super(message);
        log.error(message, this);
    }
}
