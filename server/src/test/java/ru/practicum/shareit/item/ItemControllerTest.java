package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constans.AppConstants.USER_ID_FIELD;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemServiceImpl itemService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;
    private ItemDto itemDto2;
    private ItemWithBookingDto itemDto3;
    private CommentDto commentDto;
    private CommentResponseDto commentResponseDto;


    @BeforeEach
    void beforeEach() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("Harrison")
                .email("Ford@test.com")
                .build();


        itemDto = ItemDto.builder()
                .id(1L)
                .name("XBOX")
                .description("console from Microsoft")
                .available(true)
                .build();

        itemDto2 = ItemDto.builder()
                .id(2L)
                .name("PlayStation")
                .description("Gaming console")
                .available(true)
                .build();

        itemDto3 = ItemWithBookingDto.builder()
                .id(3L)
                .name("Dendi")
                .description("Gaming console")
                .available(true)
                .owner(user)
                .build();


        Comment comment = Comment.builder()
                .id(1L)
                .author(UserMapper.toUser(user))
                .text("Don't work")
                .item(ItemMapper.toItem(itemDto3))
                .build();

        commentDto = CommentMapper.toCommentDto(comment);
        commentResponseDto = CommentMapper.toCommentResponseDto(comment);

    }

    @Test
    void addItem() throws Exception {
        when(itemService.createItem(any(ItemDto.class))).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_FIELD, 1))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Integer.class));

        verify(itemService, times(1)).createItem(itemDto);
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_FIELD, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Integer.class));

        verify(itemService, times(1)).updateItem(1L, itemDto);
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDto3);

        mvc.perform(get("/items/{itemId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_FIELD, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto3.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto3.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto3.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto3.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto3.getRequestId()), Integer.class));

        verify(itemService, times(1)).getItemById(1L, 1L);
    }

    @Test
    void getSearchItem() throws Exception {
        when(itemService.findItems(anyString())).thenReturn(List.of(itemDto2));

        mvc.perform(get("/items/search")
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto2))));

        verify(itemService, times(1)).findItems("text");
    }

    @Test
    void addComment() throws Exception {
        when(itemService.createComment(any(CommentDto.class))).thenReturn(commentResponseDto);

        mvc.perform(post("/items/{itemId}/comment", 3)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_FIELD, 1))
                .andExpect(status().isCreated())
                .andExpect(content().json(mapper.writeValueAsString(commentResponseDto)));

        verify(itemService, times(1)).createComment(any(CommentDto.class));
    }

}