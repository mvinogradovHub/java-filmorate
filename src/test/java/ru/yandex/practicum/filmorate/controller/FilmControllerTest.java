package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.utils.FilmValidator;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    Film film;
    FilmValidationException exception;

    @BeforeEach
    void init() {
        film = new Film(null,"2012","Фильм катастрофа",LocalDate.of(2022, 12, 27),Duration.ofHours(1), new ArrayList<>(List.of(new Genre(1L, "Комедия"))), new Mpa(1L,"PG"));
    }

    @Test
    void additionalValidateFilm_ReleaseDateShouldBeEarlierThan_12_28_1895() {

        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        exception = assertThrows(
                FilmValidationException.class,
                () -> FilmValidator.validateFilm(film)
        );
        assertEquals("Дата релиза фильма не может быть раньше 28.12.1895 г.", exception.getMessage());

        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> FilmValidator.validateFilm(film), "Не должно быть исключения при дате релиза 28.12.1895");
    }

    @Test
    void additionalValidateFilm_DescriptionLengthMustBeLess_201() {
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «свое");
        exception = assertThrows(
                FilmValidationException.class,
                () -> FilmValidator.validateFilm(film)
        );
        assertEquals("Длина описания не может превышать 200 символов", exception.getMessage());

        film.setDescription("А");
        assertDoesNotThrow(() -> FilmValidator.validateFilm(film), "Не должно быть исключения при при описании в одну букву");

    }

    @Test
    void additionalValidateFilm_DurationShouldBePositive() {
        film.setDuration(Duration.ofSeconds(-1));
        exception = assertThrows(
                FilmValidationException.class,
                () -> FilmValidator.validateFilm(film)
        );
        assertEquals("Продолжительность фильма не может быть отрицательной", exception.getMessage());

        film.setDuration(Duration.ofSeconds(1));
        assertDoesNotThrow(() -> FilmValidator.validateFilm(film), "Не должно быть исключения при продолжительности в 1 сек");
    }

}
