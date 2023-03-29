package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    Genre addGenre(Genre genre);
    List<Genre> getGenre();
    Genre getGenreById(Long GenreId);
    List<Genre> getGenreByFilmId (Long filmId);
}
