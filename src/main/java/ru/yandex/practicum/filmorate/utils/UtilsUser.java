package ru.yandex.practicum.filmorate.utils;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
public class UtilsUser {
    public static User WriteUserNameFromLogin(User user) {
        String name = user.getName();
        if (name == null || name.isEmpty() || name.isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пустое. Используем логин");
        }
        return user;
    }
}
