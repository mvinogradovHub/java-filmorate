package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.FilmValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {


    private HashMap<Integer, Film> films = new HashMap<>();
    private Integer id = 1;

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос к эндпоинту POST /films с телом сообщения: " + film);
        FilmValidator.validateFilm(film);
        film.setId(id);
        films.put(film.getId(), film);
        id++;
        return films.get(film.getId());
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос к эндпоинту PUT /films с телом сообщения: " + film);
        FilmValidator.validateFilm(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return films.get(film.getId());
        } else {
            throw new NotFoundException("Ошибка обновления фильма. Фильм с ID " + film.getId() + " не найден");
        }

    }

    @GetMapping
    public ArrayList<Film> getFilms() {
        log.info("Получен запрос к эндпоинту GET /films");
        return new ArrayList<>(films.values());
    }


}
