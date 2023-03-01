package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.user.UserNotFoundFriendException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.UtilsUser;

import java.util.ArrayList;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        Long receivedUserId = user.getId();
        if (receivedUserId != null && userStorage.getUserById(receivedUserId) != null) {
            return userStorage.updateUser(UtilsUser.writeUserNameFromLogin(user));
        } else {
            log.warn("Ошибка обновления пользователя. Пользователь с ID " + user.getId() + " не найден");
            throw new UserNotFoundException("Ошибка обновления пользователя. Пользователь с ID " + user.getId() + " не найден");
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
            throw new UserNotFoundException("Ошибка получения пользователя. Пользователь с ID \" + id + \" не найден");
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
        User user = userStorage.getUserById(userId);
        return userStorage.getFriends(userId);
    }

    private void checkUsers(Long userId, Long otherId, Boolean checkFriend) {
        User user = userStorage.getUserById(userId);
        User other = userStorage.getUserById(otherId);
        if (user != null && other != null) {
            if (checkFriend) {
                if (!user.getFriends().contains(otherId)) {
                    log.warn("У пользователя ID " + userId + " не найден друг с ID " + otherId);
                    throw new UserNotFoundFriendException("У пользователя ID " + userId + " не найден друг с ID " + otherId);
                }
                if (!other.getFriends().contains(userId)) {
                    log.warn("У пользователя ID " + otherId + " не найден друг с ID " + userId);
                    throw new UserNotFoundFriendException("У пользователя ID " + otherId + " не найден друг с ID " + userId);
                }
            }
            return;
        }
        log.warn("Не найден пользователь ID " + userId + " или его друг " + otherId);
        throw new UserNotFoundException("Не найден пользователь ID " + userId + " или его друг " + otherId);
    }
}
