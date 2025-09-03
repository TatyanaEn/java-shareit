package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @MockBean
    private ItemRequestServiceImpl itemRequestService;

    @MockBean
    private UserServiceImpl userService;


    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemRequestDto itemRequestDto;
    private ItemRequestWithAnswerDto itemRequestFullDto;


    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .name("Harrison")
                .email("Ford@test.com")
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("Tom")
                .email("cruise@test.com")
                .build();

        User user3 = User.builder()
                .id(3L)
                .name("Jamie")
                .email("fox@test.com")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Nintendo")
                .created(LocalDateTime.now())
                .requestor(user)
                .build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("Sega")
                .created(LocalDateTime.now())
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("XBOX")
                .description("console from Microsoft")
                .available(true)
                .owner(user)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Play Station")
                .description("Gaming console")
                .available(true)
                .owner(user2)
                .build();
        Item item3 = Item.builder()
                .id(3L)
                .name("Nintendo")
                .description("fyi")
                .available(true)
                .owner(user3)
                .request(itemRequest)
                .build();

        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestFullDto = ItemRequestMapper.toItemRequestWithAnswerDto(itemRequest);

    }

    @Test
    void addRequest() throws Exception {
        when(itemRequestService.createItemRequest(any(ItemRequestDto.class))).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("USER_ID_FIELD", 1))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class));

        verify(itemRequestService, times(1)).createItemRequest(any(ItemRequestDto.class));
    }

    @Test
    void getOwnRequests() throws Exception {
        when(itemRequestService.getItemRequestListByUserId(anyLong())).thenReturn(List.of(itemRequestFullDto));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("USER_ID_FIELD", 3))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestFullDto))));

        verify(itemRequestService, times(1)).getItemRequestListByUserId(3L);
    }

    @Test
    void getAllRequests() throws Exception {
        when(itemRequestService.getAllItemRequestList(anyLong())).thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("USER_ID_FIELD", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestDto))));

        verify(itemRequestService, times(1)).getAllItemRequestList(1L);
    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong())).thenReturn(itemRequestFullDto);

        mvc.perform(get("/requests/{requestId}", itemRequestFullDto.getId())
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("USER_ID_FIELD", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestFullDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestFullDto.getDescription()), String.class));

        verify(itemRequestService, times(1)).getItemRequestById(1L);
    }

}