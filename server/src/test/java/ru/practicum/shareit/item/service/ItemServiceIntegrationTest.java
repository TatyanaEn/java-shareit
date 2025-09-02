package ru.practicum.shareit.item.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {

    private final EntityManager em;

    private final ItemService itemService;

    private final UserServiceImpl userService;
    private final ItemRepository itemRepository;
    private Item item;

    private ItemDto itemDto;
    private ItemDto itemDto2;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .name("Harrison")
                .email("ford@test.com")
                .build();
        userDto = UserMapper.toUserDto(user);

        userDto = userService.createUser(userDto);


        itemDto = ItemDto.builder()
                .name("XBOX")
                .description("Microsoft gaming console")
                .available(true)
                .owner(userDto)
                .build();

        itemDto2 = ItemDto.builder()
                .name("Play Station")
                .description("Gaming console")
                .available(true)
                .owner(userDto)
                .build();

    }

    @AfterEach
    public void after() {
        itemRepository.deleteAll();
        userService.deleteUser(userDto.getId());

    }

    @Test
    void createItem() {
        itemService.createItem(itemDto);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item queryItem = query
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(queryItem.getName(), equalTo(itemDto.getName()));
        assertThat(queryItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(queryItem.getOwner().getName(), equalTo(itemDto.getOwner().getName()));
        assertThat(queryItem.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void getItems() {
        itemDto = itemService.createItem(itemDto);
        itemDto2 = itemService.createItem(itemDto2);

        List<ItemWithBookingDto> items = new ArrayList<>(itemService.findAllItemsByOwner(userDto.getId()));

        assertEquals(2, items.size());
        assertEquals(itemDto.getName(), items.get(0).getName());
    }


}


