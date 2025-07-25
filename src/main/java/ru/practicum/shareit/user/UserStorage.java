package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAll();

    Long createUser(User user);

    Long updateUser(User user);

    Optional<User> getUserById(Long userId);

    void deleteUser(Long userId);

}
