package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;


    @Test
    void testItemRequestDto() throws IOException {
        LocalDateTime create = LocalDateTime.of(2023, 11, 11, 11, 0);
        User user = User.builder()
                .id(1L)
                .name("Tom")
                .email("cruise@test.com")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .requestor(user)
                .description("Need something")
                .id(1L)
                .created(create)
                .build();
        ItemDto item = ItemDto.builder()
                .id(1L)
                .name("SONY Bravia")
                .description("55', gray color")
                .available(true)
                .requestId(1L)
                .build();

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Need something");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(create.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

    }
}