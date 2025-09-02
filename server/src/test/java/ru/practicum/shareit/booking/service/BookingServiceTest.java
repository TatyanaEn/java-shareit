package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class BookingServiceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            .withZone(ZoneOffset.UTC);
    @Autowired
    private BookingService bookingService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private BookingRepository bookingRepository;

    private User user;
    private User user2;
    private User user3;
    private Item item;
    private ItemDto itemDto;
    private Booking booking1;
    private Booking booking2;

    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .name("Harrison")
                .email("Ford@test.com")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("Tom")
                .email("cruise@test.com")
                .build();

        user3 = User.builder()
                .id(3L)
                .name("Jamie")
                .email("fox@test.com")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Nintendo")
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name("XBOX")
                .description("console from Microsoft")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Play Station")
                .description("Gaming console")
                .available(true)
                .owner(user2)
                .build();


        itemDto = ItemMapper.toItemDto(item);
        ItemDto itemDto2 = ItemMapper.toItemDto(item2);

        Comment comment = Comment.builder()
                .id(1L)
                .author(user)
                .createDate(LocalDateTime.now())
                .text("Don't work")
                .build();
        booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(30))
                .item(item)
                .booker(user3)
                .status(BookingStatus.WAITING)
                .build();

        booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .item(item)
                .booker(user3)
                .status(BookingStatus.APPROVED)
                .build();

        bookingRequestDto = BookingRequestDto.builder()
                .id(booking1.getId())
                .start(booking1.getStart().format(dateTimeFormatter))
                .end(booking1.getEnd().format(dateTimeFormatter))
                .status(booking1.getStatus())
                .bookerId(booking1.getBooker().getId())
                .itemId(booking1.getItem().getId())
                .build();


    }

    @Test
    void createBookingTest() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking1);

        BookingResponseDto bookingOutDtoTest = bookingService.createBooking(bookingRequestDto);

        assertEquals(bookingOutDtoTest.getItem(), itemDto);
        assertEquals(bookingOutDtoTest.getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.getBooker(), UserMapper.toUserDto(user3));

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBookingUserNotFound() {
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingRequestDto));
    }

    @Test
    void createBookingItemNotFound() {
        bookingRequestDto.setItemId(55L);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingRequestDto));

    }

    @Test
    void createBookingOwnItem() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingRequestDto));
    }

    @Test
    void createBookingItemBooked() {

        item.setAvailable(false);

        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(bookingRequestDto));
    }


    @Test
    void updateBookingTest() {
        BookingResponseDto test;

        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking1);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        test = bookingService.acceptBooking(user.getId(), booking1.getId(), true);
        assertEquals(test.getStatus(), BookingStatus.APPROVED);

        test = bookingService.acceptBooking(user.getId(), booking1.getId(), false);
        assertEquals(test.getStatus(), BookingStatus.REJECTED);


        verify(bookingRepository, times(2)).save(any(Booking.class));
    }

    @Test
    void updateBookingWrongUser() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking2));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking2);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        assertThrows(ValidationException.class, () -> bookingService.acceptBooking(user2.getId(), booking2.getId(), true));
    }

    @Test
    void updateBookingUserNotFound() {
        assertThrows(ValidationException.class, () -> bookingService.acceptBooking(44L, booking2.getId(), true));
    }

    @Test
    void updateBookingWhenBookingNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        assertThrows(NotFoundException.class, () -> bookingService.acceptBooking(1L, 55L, true));
    }


    @Test
    void getById() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        BookingResponseDto bookingOutDtoTest = bookingService.getBookingById(user.getId(), booking1.getId());

        assertEquals(bookingOutDtoTest.getItem(), itemDto);
        assertEquals(bookingOutDtoTest.getStatus(), booking1.getStatus());
        assertEquals(bookingOutDtoTest.getBooker(), UserMapper.toUserDto(user3));

    }

    @Test
    void getByIdError() {
        when(bookingRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));

        assertThrows(ValidationException.class, () -> bookingService.getBookingById(user2.getId(), 2L));
    }

    @Test
    void getByIdNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(user2.getId(), 4L));
    }


    @Test
    void getAllByUserTest() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user3));

        when(bookingRepository.findByBooker_Id(anyLong(), any(Sort.class))).thenReturn(new ArrayList<>(List.of(booking1, booking2)));
        String state = "ALL";
        List<BookingResponseDto> test = new ArrayList<>(bookingService.getBookingListByUserId(user3.getId(), state));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserDto(user3));

        when(bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Sort.class))).thenReturn(new ArrayList<>(List.of(booking1, booking2)));
        state = "CURRENT";

        test = new ArrayList<>(bookingService.getBookingListByUserId(user3.getId(), state));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserDto(user3));

        when(bookingRepository.findByBooker_IdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Sort.class))).thenReturn(new ArrayList<>(List.of(booking1, booking2)));
        state = "PAST";

        test = new ArrayList<>(bookingService.getBookingListByUserId(user3.getId(), state));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserDto(user3));

        when(bookingRepository.findByBooker_IdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Sort.class))).thenReturn(new ArrayList<>(List.of(booking1, booking2)));
        state = "FUTURE";

        test = new ArrayList<>(bookingService.getBookingListByUserId(user3.getId(), state));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserDto(user3));

        when(bookingRepository.findByBooker_IdAndStatus(anyLong(), any(String.class), any(Sort.class))).thenReturn(new ArrayList<>(List.of(booking1, booking2)));
        state = "WAITING";

        test = new ArrayList<>(bookingService.getBookingListByUserId(user3.getId(), state));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserDto(user3));

        when(bookingRepository.findByBooker_IdAndStatus(anyLong(), any(String.class), any(Sort.class))).thenReturn(new ArrayList<>(List.of(booking1, booking2)));
        state = "REJECTED";

        test = new ArrayList<>(bookingService.getBookingListByUserId(user3.getId(), state));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserDto(user3));
    }


    @Test
    void getAllByOwnerTest() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByOwner(any(Long.class))).thenReturn(new ArrayList<>(List.of(booking1)));

        String state = "ALL";

        List<BookingResponseDto> test = new ArrayList<>(bookingService.getBookingListByOwnerId(user.getId(), state));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserDto(user3));

        when(bookingRepository.findByOwnerAndStartIsBeforeAndEndIsAfter(any(Long.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(new ArrayList<>(List.of(booking1)));
        state = "CURRENT";

        test = new ArrayList<>(bookingService.getBookingListByOwnerId(user.getId(), state));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserDto(user3));

        when(bookingRepository.findByOwnerAndEndDateBefore(any(Long.class), any(LocalDateTime.class))).thenReturn(new ArrayList<>(List.of(booking1)));
        state = "PAST";

        test = new ArrayList<>(bookingService.getBookingListByOwnerId(user.getId(), state));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserDto(user3));

        when(bookingRepository.findByOwnerAndStartDateIsAfter(any(Long.class), any(LocalDateTime.class))).thenReturn(new ArrayList<>(List.of(booking1)));
        state = "FUTURE";

        test = new ArrayList<>(bookingService.getBookingListByOwnerId(user.getId(), state));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserDto(user3));

        when(bookingRepository.findByOwnerAndStatus(any(Long.class), any(String.class))).thenReturn(new ArrayList<>(List.of(booking1)));
        state = "WAITING";

        test = new ArrayList<>(bookingService.getBookingListByOwnerId(user.getId(), state));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserDto(user3));

        when(bookingRepository.findByOwnerAndStatus(any(Long.class), any(String.class))).thenReturn(new ArrayList<>(List.of(booking1)));
        state = "REJECTED";

        test = new ArrayList<>(bookingService.getBookingListByOwnerId(user.getId(), state));

        assertEquals(test.get(0).getId(), booking1.getId());
        assertEquals(test.get(0).getStatus(), booking1.getStatus());
        assertEquals(test.get(0).getBooker(), UserMapper.toUserDto(user3));
    }

    @Test
    void getAllBookingsForAllItemsByOwnerIdNotHaveItems() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong())).thenReturn(List.of());

        assertEquals(new ArrayList<>(), bookingService.getBookingListByOwnerId(user.getId(), "ALL"));
    }
}