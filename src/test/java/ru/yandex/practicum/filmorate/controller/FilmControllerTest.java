package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.Exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.Validate;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    Film film;

    @Test
    void validateFilm() {

        film = Film.builder().name("2012")
                .description("Фильм катастрофа")
                .duration(Duration.ofHours(1))
                .releaseDate(LocalDate.of(1895, 12, 27))
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> Validate.AdditionalValidateFilm(film)
        );
        assertEquals("Дата релиза фильма не может быть раньше 28.12.1895 г.", exception.getMessage());

        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> Validate.AdditionalValidateFilm(film),"Не должно быть исключения при дате релиза 28.12.1895");

        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «свое");
        exception = assertThrows(
                ValidationException.class,
                () -> Validate.AdditionalValidateFilm(film)
        );
        assertEquals("Длина описания не может превышать 200 символов", exception.getMessage());

        film.setDescription("А");
        assertDoesNotThrow(() -> Validate.AdditionalValidateFilm(film),"Не должно быть исключения при при описании в одну букву");


        film.setDuration(Duration.ofSeconds(-1));
        exception = assertThrows(
                ValidationException.class,
                () -> Validate.AdditionalValidateFilm(film)
        );
        assertEquals("Продолжительность фильма не может быть отрицательной", exception.getMessage());

        film.setDuration(Duration.ofSeconds(1));
        assertDoesNotThrow(() -> Validate.AdditionalValidateFilm(film),"Не должно быть исключения при продолжительности в 1 сек");
    }

}
