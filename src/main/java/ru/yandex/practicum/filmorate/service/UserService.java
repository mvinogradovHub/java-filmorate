package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.UtilsUser;

import java.util.ArrayList;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User addUser(User user) {
        UtilsUser.writeUserNameFromLogin(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        Long receivedUserId = user.getId();
        if (receivedUserId != null && userStorage.getUserById(receivedUserId) != null) {
            return userStorage.updateUser(UtilsUser.writeUserNameFromLogin(user));
        } else {
            log.warn("Ошибка обновления пользователя. Пользователь с ID " + user.getId() + " не найден");
            throw new NotFoundException("Ошибка обновления пользователя. Пользователь с ID " + user.getId() + " не найден");
        }
    }

    public ArrayList<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUserById(Long id) {
        User receivedUser = userStorage.getUserById(id);
        if (receivedUser != null) {
            return receivedUser;
        } else {
            log.warn("Ошибка получения пользователя. Пользователь с ID " + id + " не найден");
            throw new NotFoundException("Ошибка получения пользователя. Пользователь с ID \" + id + \" не найден");
        }
    }

    public void addFriend(Long userId, Long friendId) {
        checkUsers(userId, friendId, false);
        userStorage.addFriend(userId, friendId);

    }

    public void deleteFriend(Long userId, Long friendId) {
        checkUsers(userId, friendId, true);
        userStorage.deleteFriend(userId, friendId);
    }

    public Set<User> getCommonFriends(Long userId, Long otherId) {
        checkUsers(userId, otherId, false);
        return userStorage.getCommonFriends(userId, otherId);
    }

    public Set<User> getFriends(Long userId) {
        return userStorage.getFriends(userId);
    }

    private void checkUsers(Long userId, Long otherId, Boolean checkFriend) {
        User user = userStorage.getUserById(userId);
        User other = userStorage.getUserById(otherId);
        if (user != null && other != null) {
            if (checkFriend) {
                if (!user.getFriends().contains(otherId)) {
                    log.warn("У пользователя ID " + userId + " не найден друг с ID " + otherId);
                    throw new NotFoundException("У пользователя ID " + userId + " не найден друг с ID " + otherId);
                }
            }
            return;
        }
        log.warn("Не найден пользователь ID " + userId + " или его друг " + otherId);
        throw new NotFoundException("Не найден пользователь ID " + userId + " или его друг " + otherId);
    }
}
