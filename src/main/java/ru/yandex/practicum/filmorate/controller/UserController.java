package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.UtilsUser;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private HashMap<Integer, User> users = new HashMap<>();
    private int id = 1;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        log.info("Получен запрос к эндпоинту POST /users с телом сообщения: "+ user);
        if (user != null && (!users.containsKey(user.getId()) || user.getId() == null)) {
            user.setId(id);
            users.put(id, UtilsUser.WriteUserNameFromLogin(user));
            id++;
            return users.get(user.getId());
        } else {
            log.warn("Ошибка добавления пользователя. Пользователь с ID " + user.getId() + " уже существует");
            throw new ValidationException("Ошибка добавления пользователя. Пользователь с ID " + user.getId() + " уже существует");
        }

    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws NotFoundException {
        log.info("Получен запрос к эндпоинту PUT /users с телом сообщения: "+ user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(),UtilsUser.WriteUserNameFromLogin(user));
            return users.get(user.getId());
        } else {
            log.warn("Ошибка обновления пользователя. Пользователь с ID " + user.getId() + " не найден");
            throw new NotFoundException("Ошибка обновления пользователя. Пользователь с ID " + user.getId() + " не найден");
        }

    }

    @GetMapping
    public ArrayList<User> getUsers() {
        log.info("Получен запрос к эндпоинту GET /users");
        return new ArrayList<>(users.values());
    }



}
