package ru.practicum.shareit.exception;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParameterNotValidException extends IllegalArgumentException {
    private String parameter;
    private String reason;

    public ParameterNotValidException(String parameter, String reason) {
        super();
        this.parameter = parameter;
        this.reason = reason;
    }
}
