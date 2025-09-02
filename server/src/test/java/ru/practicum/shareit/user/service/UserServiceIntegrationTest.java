package ru.practicum.shareit.user.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {

    private final EntityManager em;

    private final UserService userService;


    @Test
    void createUser() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("Harrison")
                .email("ford@test.com")
                .build();

        userService.createUser(user);

        List<User> allUsers = em.createQuery("SELECT u FROM User u", User.class)
                .getResultList();


        assertEquals(1, allUsers.size());
        assertThat(allUsers.get(0).getName(), equalTo(user.getName()));
        assertThat(allUsers.get(0).getEmail(), equalTo(user.getEmail()));


    }

    @Test
    void getAllUsers() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("Harrison")
                .email("ford@test.com")
                .build();
        UserDto user2 = UserDto.builder()
                .id(2L)
                .name("Marlon")
                .email("brando@test.com")
                .build();

        userService.createUser(user);
        userService.createUser(user2);

        List<User> allUsers = em.createQuery("SELECT u FROM User u", User.class)
                .getResultList();
        assertEquals(2, allUsers.size());
        assertThat(allUsers.get(0).getName(), equalTo(user.getName()));
        assertThat(allUsers.get(0).getEmail(), equalTo(user.getEmail()));


    }
}