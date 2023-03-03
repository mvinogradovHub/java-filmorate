package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class FilmsComparator implements Comparator<Film> {
    @Override
    public int compare(Film film1, Film film2) {
        if (film1.getIdUsersLike() == null) {
            return -1;
        }
        if (film2.getIdUsersLike() == null) {
            return 1;
        }
        if (film1.getIdUsersLike() == null && film2.getIdUsersLike() == null) {
            return 0;
        }
        return Integer.compare(film1.getIdUsersLike().size(), film2.getIdUsersLike().size());

    }
}
