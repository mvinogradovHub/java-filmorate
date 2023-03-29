package ru.yandex.practicum.filmorate.storage.user.impl.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.UtilsUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> users = new HashMap<>();
    private Long id = 1L;

    public User addUser(User user) {
        user.setId(id);
        users.put(id, UtilsUser.writeUserNameFromLogin(user));
        id++;
        return users.get(user.getId());
    }

    public User updateUser(User user) {
        users.put(user.getId(), UtilsUser.writeUserNameFromLogin(user));
        return users.get(user.getId());
    }


    public ArrayList<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserById(Long id) {
        return users.get(id);
    }

    public Set<User> getFriends(Long id) {
        User user = users.get(id);
        return users.entrySet()
                .stream()
                .filter(x -> user.getFriends().contains(x.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(Long userId, Long otherId) {
        return users.entrySet()
                .stream()
                .filter(x -> users.get(userId).getFriends().contains(x.getKey()))
                .filter(x -> users.get(otherId).getFriends().contains(x.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());

    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        users.put(user.getId(),user);
        users.put(friend.getId(),friend);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        users.put(user.getId(),user);
        users.put(friend.getId(),friend);
    }

}
