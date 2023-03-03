package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        Long receivedFilm = film.getId();
        if (receivedFilm != null && filmStorage.getFilmById(receivedFilm) != null) {
            return filmStorage.updateFilm(film);
        } else {
            log.warn("Ошибка обновления фильма. Фильм с ID " + film.getId() + " не найден");
            throw new NotFoundException("Ошибка обновления фильма. Фильм с ID " + film.getId() + " не найден");
        }
    }

    public ArrayList<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Long id) {
        Film receivedFilm = filmStorage.getFilmById(id);
        if (receivedFilm != null) {
            return receivedFilm;
        } else {
            log.warn("Ошибка получения фильма. Фильм с ID " + id + " не найден");
            throw new NotFoundException("Ошибка получения фильма. Фильм с ID " + id + " не найден");
        }
    }

    public void addLike(Long filmId, Long userId) {
        checkFilmAndUser(filmId, userId);
        Film film = filmStorage.getFilmById(filmId);
        film.getIdUsersLike().add(userId);
        filmStorage.updateFilm(film);
    }

    public void deleteLike(Long filmId, Long userId) {
        checkFilmAndUser(filmId, userId);
        Film film = filmStorage.getFilmById(filmId);
        film.getIdUsersLike().remove(userId);
        filmStorage.updateFilm(film);
    }

    public List<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }

    private void checkFilmAndUser(Long filmId, Long userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.warn("Не найден пользователь ID " + userId);
            throw new NotFoundException("Не найден пользователь ID " + userId);
        }
        if (film == null) {
            log.warn("Не найден фильм ID " + filmId);
            throw new NotFoundException("Не найден фильм ID " + userId);
        }

    }

}
