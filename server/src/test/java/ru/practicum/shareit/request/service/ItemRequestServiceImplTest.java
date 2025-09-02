package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
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
class ItemRequestServiceImplTest {
    @Autowired
    private ItemRequestService itemRequestService;

    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    private User user;
    private User user2;
    private User user3;
    private ItemRequest itemRequest;
    private ItemRequest itemRequest2;
    private Item item;
    private Item item2;
    private Item item3;

    private ItemRequestDto itemRequestDto;


    @BeforeEach
    void setUp() {
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

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Nintendo")
                .created(LocalDateTime.now())
                .requestor(user)
                .build();
        itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("Sega")
                .created(LocalDateTime.now())
                .build();

        item = Item.builder()
                .id(1L)
                .name("XBOX")
                .description("console from Microsoft")
                .available(true)
                .owner(user)
                .build();

        item2 = Item.builder()
                .id(2L)
                .name("Play Station")
                .description("Gaming console")
                .available(true)
                .owner(user2)
                .build();
        item3 = Item.builder()
                .id(3L)
                .name("Nintendo")
                .description("fyi")
                .available(true)
                .owner(user3)
                .request(itemRequest)
                .build();

        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);


    }

    @Test
    void addRequest() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto itemRequestDtoTest = itemRequestService.createItemRequest(itemRequestDto);

        assertEquals(itemRequestDtoTest.getId(), itemRequest.getId());
        assertEquals(itemRequestDtoTest.getDescription(), itemRequest.getDescription());

        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getOwnRequests() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user3));
        when(itemRequestRepository.findByRequestor_Id(anyLong(), any(Sort.class))).thenReturn(new ArrayList<>(List.of(itemRequest)));
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findAllByRequestId(any(Long.class))).thenReturn(List.of(item3));

        ItemRequestWithAnswerDto test = new ArrayList<>(itemRequestService.getItemRequestListByUserId(user.getId())).get(0);


        assertEquals(test.getItems().get(0).getId(), item3.getId());
        assertEquals(test.getItems().get(0).getName(), item3.getName());
        assertEquals(test.getItems().get(0).getDescription(), item3.getDescription());
        assertEquals(test.getItems().get(0).getAvailable(), item3.getAvailable());

        verify(itemRequestRepository, times(1)).findByRequestor_Id(anyLong(), any(Sort.class));
    }

    @Test
    void getAllRequests() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestor_IdNot(any(Long.class), any(Sort.class))).thenReturn(new ArrayList<>(List.of(itemRequest)));
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findAllByRequestId(any(Long.class))).thenReturn(List.of(item3));

        ItemRequestDto test = new ArrayList<>(itemRequestService.getAllItemRequestList(user.getId())).get(0);
        assertEquals(test.getId(), itemRequest.getId());
        assertEquals(test.getDescription(), itemRequest.getDescription());

        verify(itemRequestRepository, times(1)).findByRequestor_IdNot(any(Long.class), any(Sort.class));
    }


    @Test
    void getRequestById() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item3));
        when(itemRequestRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequestWithAnswerDto test = itemRequestService.getItemRequestById(itemRequest.getId());

        assertEquals(test.getId(), itemRequest.getId());
        assertEquals(test.getDescription(), itemRequest.getDescription());
        assertEquals(test.getItems().get(0).getId(), item3.getId());
        assertEquals(test.getItems().get(0).getOwner().getId(), user3.getId());

        verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    void getByIdRequestNotFound() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item3));
        when(itemRequestRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(5L));

        verify(itemRequestRepository, times(1)).findById(5L);
    }

}