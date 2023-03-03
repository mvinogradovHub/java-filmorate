package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {
    public static void validateFilm(Film film) {
        if (film.getDescription().length() > 200) {
            throw new FilmValidationException("Длина описания не может превышать 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new FilmValidationException("Дата релиза фильма не может быть раньше 28.12.1895 г.");
        } else if (film.getDuration().isNegative()) {
            throw new FilmValidationException("Продолжительность фильма не может быть отрицательной");
        }

    }
}
