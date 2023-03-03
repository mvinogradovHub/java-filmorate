package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.UtilsUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User addUser(User user) {
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
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);

    }

    public void deleteFriend(Long userId, Long friendId) {
        checkUsers(userId, friendId, true);
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
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
                    throw new NotFoundException("У пользователя ID " + userId + " не найден друг с ID " + otherId);
                }
                if (!other.getFriends().contains(userId)) {
                    log.warn("У пользователя ID " + otherId + " не найден друг с ID " + userId);
                    throw new NotFoundException("У пользователя ID " + otherId + " не найден друг с ID " + userId);
                }
            }
            return;
        }
        log.warn("Не найден пользователь ID " + userId + " или его друг " + otherId);
        throw new NotFoundException("Не найден пользователь ID " + userId + " или его друг " + otherId);
    }
}
