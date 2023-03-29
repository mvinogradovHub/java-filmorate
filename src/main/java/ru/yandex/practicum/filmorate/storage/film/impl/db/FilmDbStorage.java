package ru.yandex.practicum.filmorate.storage.film.impl.db;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;


@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, MpaDbStorage mpaDbStorage, GenreDbStorage genreDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
    }

    public Film addFilm(Film film) {
        String sqlInsertFilm = "INSERT INTO FILM (FILM_ID,NAME,RELEASE_DATE,DESCRIPTION, DURATION, MPA_ID) VALUES (?,?,?,?,?,?)";
        Long filmId = getNewFilmId();
        jdbcTemplate.update(sqlInsertFilm, filmId, film.getName(), film.getReleaseDate(), film.getDescription(), film.getDuration().getSeconds(), film.getMpa().getId());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String sqlInsertGenre = "INSERT INTO FILM_GENRE (FILM_ID,GENRE_ID) VALUES (?,?)";
                jdbcTemplate.update(sqlInsertGenre, filmId, genre.getId());
            }
        }
        return getFilmById(filmId);
    }

    private Long getNewFilmId() {
        String sql = "SELECT FILM_ID FROM FILM ORDER BY FILM_ID DESC LIMIT 1";
        Optional<Long> filmId = jdbcTemplate.query(sql, (rs, rowNum) -> makeLustId(rs)).stream().findFirst();
        return filmId.map(aLong -> aLong + 1).orElse(1L);

    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE FILM SET NAME = ?,DESCRIPTION = ?, RELEASE_DATE = ?,DURATION= ?, MPA_ID = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration().getSeconds(), film.getMpa().getId(), film.getId());
        String sqlDel = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlDel, film.getId());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String sqlInsertGenre = "INSERT INTO FILM_GENRE (FILM_ID,GENRE_ID) VALUES (?,?)";
                jdbcTemplate.update(sqlInsertGenre, film.getId(), genre.getId());
            }
        }
        return getFilmById(film.getId());
    }

    @Override
    public List<Film> getFilms() {
        String sqlSelectFilm = "SELECT * FROM FILM";
        try {
            return jdbcTemplate.query(sqlSelectFilm, (rs, rowNum) -> makeFilm(rs));
        } catch (NoSuchElementException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Film getFilmById(Long id) {
        String sqlSelectFilm = "SELECT * FROM FILM WHERE FILM_ID = ?";
        List<Film> films = jdbcTemplate.query(sqlSelectFilm, (rs, rowNum) -> makeFilm(rs), id);
        if (films.isEmpty()) {
            return null;
        }
        return films.get(0);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sql = "SELECT F.*, COUNT(FL.FILM_LIKE_ID) AS LIKES_COUNT\n" +
                "FROM FILM F\n" +
                "LEFT JOIN FILM_LIKE FL ON F.FILM_ID = FL.FILM_ID\n" +
                "GROUP BY F.FILM_ID\n" +
                "ORDER BY LIKES_COUNT DESC\n" +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
        if (films.isEmpty()) {
            return Collections.emptyList();
        }
        return films;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO FILM_LIKE (FILM_ID,USER_ID) VALUES (?,?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sql = "DELETE FROM FILM_LIKE WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public Set<Long> getFilmLikes(Long filmId) {
        String sql = "SELECT USER_ID FROM FILM_LIKE WHERE FILM_ID = ?";
        List<Long> filmLikesId = jdbcTemplate.query(sql, (rs, rowNum) -> makeLikesId(rs), filmId);
        if (filmLikesId.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(filmLikesId);

    }


    private Film makeFilm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("FILM_ID");
        String name = rs.getString("NAME");
        String description = rs.getString("DESCRIPTION");
        LocalDate releaseDate = rs.getDate("RELEASE_DATE").toLocalDate();
        Duration duration = Duration.ofSeconds(rs.getInt("DURATION"));
        Mpa mpa = mpaDbStorage.getMpaById(rs.getLong("MPA_ID"));
        List<Genre> genres = genreDbStorage.getGenreByFilmId(id);
        Set<Long> likes = getFilmLikes(id);
        Film film = new Film(id, name, description, releaseDate, duration, genres, mpa);
        film.setIdUsersLike(likes);
        return film;
    }

    private Long makeLustId(ResultSet rs) throws SQLException {
        return rs.getLong("FILM_ID");
    }

    private Long makeLikesId(ResultSet rs) throws SQLException {
        return rs.getLong("USER_ID");
    }


}
