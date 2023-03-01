package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    ArrayList<Film> getFilms();

    Film getFilmById(Long id);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    List<Film> getPopularFilms(Integer count);

}
