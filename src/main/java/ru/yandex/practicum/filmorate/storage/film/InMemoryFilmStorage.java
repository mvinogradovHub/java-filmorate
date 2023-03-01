package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.film.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.FilmValidator;
import ru.yandex.practicum.filmorate.utils.FilmsComparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Long, Film> films = new HashMap<>();
    private Long id = 1L;

    public Film addFilm(Film film) {
        FilmValidator.validateFilm(film);
        film.setId(id);
        films.put(film.getId(), film);
        id++;
        return films.get(film.getId());
    }

    public Film updateFilm(Film film) {
        FilmValidator.validateFilm(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return films.get(film.getId());
        } else {
            throw new FilmNotFoundException("Ошибка обновления фильма. Фильм с ID " + film.getId() + " не найден");
        }
    }

    public ArrayList<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    public void addLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        film.addLike(userId);
    }
    public void deleteLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        film.deleteLike(userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        return films.values()
                .stream()
                .sorted(new FilmsComparator().reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilmById (Long id) {
        return films.get(id);
    }
}
