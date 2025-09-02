package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRequestRepository itemRequestRepository;


    private User user;
    private User user2;
    private Item item;
    private Item item2;
    private ItemDto itemDto;
    private ItemDto itemDto2;
    private Comment comment;
    private CommentDto commentDto;
    private ItemRequest itemRequest;
    private Booking firstBooking;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("Harrison")
                .email("ford@test.com")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("Tom")
                .email("cruise@test.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("ItemRequest 1")
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name("XBOX")
                .description("Microsoft gaming console")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();

        item2 = Item.builder()
                .id(2L)
                .name("Play Station")
                .description("Gaming console")
                .available(true)
                .owner(user2)
                .build();


        itemDto = ItemMapper.toItemDto(item);
        itemDto2 = ItemMapper.toItemDto(item2);

        comment = Comment.builder()
                .id(1L)
                .author(user2)
                .createDate(LocalDateTime.now())
                .text("Don't work!")
                .item(item)
                .build();

        commentDto = CommentMapper.toCommentDto(comment);

        firstBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(user2)
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void createItemTest() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto test = itemService.createItem(itemDto);

        assertEquals(test.getId(), itemDto.getId());
        assertEquals(test.getDescription(), itemDto.getDescription());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItemUserNotFound() {
        assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto));
    }

    @Test
    void updateItemTest() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of(item));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto itemDtoTest = itemService.updateItem(user.getId(), itemDto);

        assertEquals(itemDtoTest.getId(), itemDto.getId());
        assertEquals(itemDtoTest.getDescription(), itemDto.getDescription());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItemUserNotFound() {
        assertThrows(NotFoundException.class, () -> itemService.updateItem(14L, itemDto));

    }

    @Test
    void updateItemWhenItemNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(16L, itemDto));

    }

    @Test
    void updateItemNotBelongUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item2));

        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(user.getId(), itemDto2));
    }


    @Test
    void getItemById() {
        Booking lastBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .status(BookingStatus.APPROVED)
                .item(item)
                .booker(user2)
                .build();

        Booking nextBooking = Booking.builder()
                .id(3L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.APPROVED)
                .booker(user2)
                .item(item)
                .build();
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findTop1ByItem_IdAndStartIsBefore(
                eq(item.getId()), any(LocalDateTime.class)))
                .thenReturn(lastBooking);
        when(bookingRepository.findTop1ByItem_IdAndStartIsAfter(
                eq(item.getId()), any(LocalDateTime.class)))
                .thenReturn(nextBooking);
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));

        ItemWithBookingDto itemDtoTest = itemService.getItemById(user.getId(), item.getId());

        assertEquals(itemDtoTest.getId(), item.getId());
        assertEquals(itemDtoTest.getDescription(), item.getDescription());
        assertEquals(itemDtoTest.getAvailable(), item.getAvailable());
        assertEquals(itemDtoTest.getRequestId(), item.getRequest().getId());

        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void getByIdWhenItemNotFound() {
        assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 43L));
    }

    @Test
    void getItems() {
        Booking lastBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().minusHours(2))
                .end(LocalDateTime.now().minusHours(1))
                .status(BookingStatus.APPROVED)
                .item(item)
                .booker(user2)
                .build();

        Booking nextBooking = Booking.builder()
                .id(3L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.APPROVED)
                .booker(user2)
                .item(item)
                .build();
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(new ArrayList<>(List.of(item)));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));
        //when(bookingRepository.findLastBookingsForItems(eq(List.of(1)), any(LocalDateTime.class), eq(BookingStatus.APPROVED))).thenReturn(List.of(lastBooking));
        //when(bookingRepository.findNextBookingsForItems(eq(List.of(1)), any(LocalDateTime.class), eq(BookingStatus.APPROVED))).thenReturn(List.of(nextBooking));
        ItemWithBookingDto itemDtoTest = new ArrayList<>(itemService.findAllItemsByOwner(user.getId())).get(0);

        assertEquals(itemDtoTest.getId(), item.getId());
        assertEquals(itemDtoTest.getDescription(), item.getDescription());
        assertEquals(itemDtoTest.getAvailable(), item.getAvailable());
        assertEquals(itemDtoTest.getRequestId(), item.getRequest().getId());

        verify(itemRepository, times(1)).findAllByOwnerId(anyLong());
    }

    @Test
    void searchItem() {
        when(itemRepository.search(anyString())).thenReturn(new ArrayList<>(List.of(item)));

        ItemDto itemDtoTest = new ArrayList<>(itemService.findItems("text")).get(0);

        assertEquals(itemDtoTest.getId(), item.getId());
        assertEquals(itemDtoTest.getDescription(), item.getDescription());
        assertEquals(itemDtoTest.getAvailable(), item.getAvailable());
        assertEquals(itemDtoTest.getRequestId(), item.getRequest().getId());

        verify(itemRepository, times(1)).search(anyString());
    }

    @Test
    void searchItemEmptyText() {
        Collection<ItemDto> itemDtoTest = itemService.findItems("");

        assertTrue(itemDtoTest.isEmpty());

        verify(itemRepository, times(0)).search(anyString());
    }

    @Test
    void addComment() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));


        when(bookingRepository.findByBooker_IdAndItem_id(
                anyLong(), anyLong()
        )).thenAnswer(invocation -> firstBooking);


        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentResponseDto commentDtoTest = itemService.createComment(commentDto);

        assertEquals(commentDtoTest.getId(), comment.getId());
        assertEquals(commentDtoTest.getText(), comment.getText());
        assertEquals(commentDtoTest.getAuthorName(), comment.getAuthor().getName());

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addCommentNotAvailable() {

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        when(bookingRepository.findByBooker_IdAndEndIsBefore(
                anyLong(), any(LocalDateTime.class), any(Sort.class)
        )).thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class, () -> itemService.createComment(commentDto));
    }

    @Test
    void addCommentWhenUserNotFound() {
        assertThrows(ValidationException.class, () -> itemService.createComment(commentDto));
    }

    @Test
    void addCommentWhenItemNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class, () -> itemService.createComment(commentDto));

    }
}
