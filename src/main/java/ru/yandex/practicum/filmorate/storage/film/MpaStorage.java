package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    Mpa addMpa(Mpa mpa);
    List<Mpa> getMpa();
    Mpa getMpaById(Long id);
}
