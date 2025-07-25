package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.utils.GenratorIds;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users;

    @Override
    public Collection<User> findAll() {
        return users.values().stream()
                .map(user -> User.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .toList();
    }

    @Override
    public Long createUser(User user) {
        User newUser = User.builder()
                .id(GenratorIds.getNextId(users))
                .name(user.getName())
                .email(user.getEmail())
                .build();
        users.put(newUser.getId(), newUser);

        return newUser.getId();
    }

    @Override
    public Long updateUser(User newUser) {
        User oldUser = users.get(newUser.getId());
        if (!(newUser.getEmail() == null || newUser.getEmail().isBlank()))
            oldUser.setEmail(newUser.getEmail());
        if (!(newUser.getName() == null || newUser.getName().isBlank()))
            oldUser.setName(newUser.getName());
        users.put(oldUser.getId(), oldUser);
        return oldUser.getId();
    }


    @Override
    public Optional<User> getUserById(Long userId) {
        User userFS = users.get(userId);
        if (userFS == null)
            return Optional.empty();
        return Optional.ofNullable(User.builder()
                .id(userId)
                .name(userFS.getName())
                .email(userFS.getEmail())
                .build());
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

}
