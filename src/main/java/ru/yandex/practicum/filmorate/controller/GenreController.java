package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public List<Genre> getGenres() {
        log.info("Получен запрос к эндпоинту GET /genres");
        return genreService.getGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenresById(@PathVariable Long id) {
        log.info("Получен запрос к эндпоинту GET /genres/{id}");
        return genreService.getGenreById(id);
    }
}
