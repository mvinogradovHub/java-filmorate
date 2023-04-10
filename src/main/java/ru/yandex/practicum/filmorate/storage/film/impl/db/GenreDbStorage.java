package ru.yandex.practicum.filmorate.storage.film.impl.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre addGenre(Genre genre) {
        String sqlInsertGenre = "INSERT INTO GENRE (NAME) VALUES (?)";
        jdbcTemplate.update(sqlInsertGenre, genre.getName());
        return getGenreById(genre.getId());
    }

    @Override
    public List<Genre> getGenre() {
        String sqlSelectGenre = "SELECT * FROM GENRE";
        List<Genre> genres = jdbcTemplate.query(sqlSelectGenre, (rs, rowNum) -> makeGenre(rs));
        if (genres.isEmpty()) {
            return Collections.emptyList();
        }
        return genres;
    }

    @Override
    public Genre getGenreById(Long genreId) {
        String sqlSelectGenre = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
        List<Genre> genre = jdbcTemplate.query(sqlSelectGenre, (rs, rowNum) -> makeGenre(rs), genreId);
        if (genre.isEmpty()) {
            return null;
        }
        return genre.get(0);
    }

    @Override
    public List<Genre> getGenreByFilmId(Long filmId) {
        String sqlSelectGenre = "SELECT * FROM GENRE WHERE GENRE_ID IN (SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?)";
        List<Genre> genres = jdbcTemplate.query(sqlSelectGenre, (rs, rowNum) -> makeGenre(rs), filmId);
        if (genres.isEmpty()) {
            return Collections.emptyList();
        }
        return genres;
    }

    private Genre makeGenre(ResultSet res) throws SQLException {
        Long id = res.getLong("GENRE_ID");
        String name = res.getString("NAME");
        return new Genre(id, name);
    }
}
