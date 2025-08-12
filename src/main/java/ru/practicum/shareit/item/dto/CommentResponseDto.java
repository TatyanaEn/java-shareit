package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponseDto {
    Long id;
    @NotBlank
    @NotNull
    @Size(max = 2000)
    String text;
    String authorName;
    private LocalDateTime created;
}

