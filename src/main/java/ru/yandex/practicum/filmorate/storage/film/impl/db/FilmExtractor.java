package ru.yandex.practicum.filmorate.storage.film.impl.db;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Component
public class FilmExtractor implements ResultSetExtractor<List<Film>> {
    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Set<Film> films = new HashSet<>();
        List<Genre> genres = new ArrayList<>();
        Set<Long> likes = new HashSet<>();
        while (rs.next()) {
            Long id = rs.getLong("FILM_ID");
            String name = rs.getString("NAME");
            String description = rs.getString("DESCRIPTION");
            LocalDate releaseDate = rs.getDate("RELEASE_DATE").toLocalDate();
            Duration duration = Duration.ofSeconds(rs.getInt("DURATION"));
            Mpa mpa = new Mpa(rs.getLong("MPA_ID"), rs.getString("MPA_NAME"));
            Long likeId = rs.getLong("LIKE_USER_ID");
            Genre genre = new Genre(rs.getLong("GENRE_ID"), rs.getString("GENRE_NAME"));
            Film film = new Film(id, name, description, releaseDate, duration, mpa);
            if (films.add(film)) {
                likes = new HashSet<>();
                genres = new ArrayList<>();
                film.setGenres(genres);
                film.setIdUsersLike(likes);
            }
            if (likeId != 0) {
                likes.add(likeId);
            }
            if (genre.getId() != 0 && !genres.contains(genre)) {
                genres.add(genre);
            }
        }
        if (films.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(films);
    }

}
