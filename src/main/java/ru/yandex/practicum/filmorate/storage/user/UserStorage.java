package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Set;

public interface UserStorage {
    User addUser(User user);

    User updateUser(User user);

    ArrayList<User> getUsers();

    User getUserById(Long id);

    Set<User> getFriends(Long id);

    Set<User> getCommonFriends(Long userId, Long otherId);

}
