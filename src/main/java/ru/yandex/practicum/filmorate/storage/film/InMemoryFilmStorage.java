package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.FilmsComparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Long, Film> films = new HashMap<>();
    private Long id = 1L;

    public Film addFilm(Film film) {
        film.setId(id);
        films.put(film.getId(), film);
        id++;
        return films.get(film.getId());
    }

    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    public ArrayList<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    public List<Film> getPopularFilms(Integer count) {
        return films.values()
                .stream()
                .sorted(new FilmsComparator().reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilmById(Long id) {
        return films.get(id);
    }
}
