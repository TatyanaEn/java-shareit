package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.constans.AppConstants.USER_ID_FIELD;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            .withZone(ZoneOffset.UTC);

    @MockBean
    private BookingServiceImpl bookingService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private Booking booking1;
    private Booking booking2;

    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingDto;

    @BeforeEach
    void beforeEach() {
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

        booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(15))
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
        bookingDto = BookingMapper.toBookingDto(booking1);

        bookingRequestDto = BookingRequestDto.builder()
                .id(1L)
                .bookerId(3L)
                .itemId(1L)
                .start(booking1.getStart().format(dateTimeFormatter))
                .end(booking1.getEnd().format(dateTimeFormatter))
                .status(BookingStatus.WAITING)
                .build();

    }

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.createBooking(any(BookingRequestDto.class))).thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_FIELD, 3))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), BookingStatus.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class));

        verify(bookingService, times(1)).createBooking(bookingRequestDto);
    }


    @Test
    void getByIdTest() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID_FIELD, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), BookingStatus.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class));

        verify(bookingService, times(1)).getBookingById(1L, 1L);
    }

    @Test
    void getAllByUserTest() throws Exception {
        when(bookingService.getBookingListByUserId(anyLong(), anyString())).thenAnswer(invocation -> {
            List<Booking> bookings = new ArrayList<>();
            bookings.add(booking1);
            bookings.add(booking2);
            return bookings;
        });
        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(5))
                        .header(USER_ID_FIELD, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(booking1, booking2))));

        verify(bookingService, times(1)).getBookingListByUserId(1L, "ALL");
    }

    @Test
    void getAllByOwnerTest() throws Exception {
        when(bookingService.getBookingListByOwnerId(anyLong(), anyString())).thenAnswer(invocation -> {
            List<Booking> bookings = new ArrayList<>();
            bookings.add(booking1);
            bookings.add(booking2);
            return bookings;
        });
        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(5))
                        .header(USER_ID_FIELD, 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(booking1, booking2))));

        verify(bookingService, times(1)).getBookingListByOwnerId(1L, "ALL");
    }

}