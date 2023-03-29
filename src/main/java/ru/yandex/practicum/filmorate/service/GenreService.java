package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> getGenres() {
        return genreStorage.getGenre();
    }

    public Genre getGenreById(Long id) {
        Genre receivedGenre = genreStorage.getGenreById(id);
        if (receivedGenre != null) {
            return receivedGenre;
        } else {
            log.warn("Ошибка получения жанра. Жанр с ID " + id + " не найден");
            throw new NotFoundException("Ошибка получения жанра. Жанр с ID " + id + " не найден");
        }
    }
}
