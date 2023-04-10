package ru.yandex.practicum.filmorate.storage.film.impl.db;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private final FilmExtractor filmExtractor;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmExtractor filmExtractor) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmExtractor = filmExtractor;
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
                String sqlInsertGenre = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID)\n" +
                        "SELECT ?, ?\n" +
                        "WHERE NOT EXISTS (\n" +
                        "SELECT 1 \n" +
                        "FROM FILM_GENRE\n" +
                        "WHERE FILM_ID =? AND GENRE_ID =?)";
                jdbcTemplate.update(sqlInsertGenre, film.getId(), genre.getId(),film.getId(), genre.getId());
            }
        }
        return getFilmById(film.getId());
    }

    @Override
    public List<Film> getFilms() {
        String sqlSelectFilm = "SELECT f.*,m.NAME AS MPA_NAME, fg.GENRE_ID, g.NAME AS GENRE_NAME,fl.USER_ID AS LIKE_USER_ID\n" +
                "FROM FILM f\n" +
                "LEFT JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID\n" +
                "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID \n" +
                "LEFT JOIN FILM_LIKE fl ON f.FILM_ID = fl.FILM_ID \n" +
                "LEFT JOIN MPA m ON f.MPA_ID = m.MPA_ID\n" +
                "ORDER BY f.FILM_ID ASC";
        return  jdbcTemplate.query(sqlSelectFilm, filmExtractor);
    }

    @Override
    public Film getFilmById(Long id) {
        String sqlSelectFilm = "SELECT f.*,m.NAME AS MPA_NAME, fg.GENRE_ID, g.NAME AS GENRE_NAME,fl.USER_ID AS LIKE_USER_ID\n" +
                "FROM FILM f\n" +
                "LEFT JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID\n" +
                "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID \n" +
                "LEFT JOIN FILM_LIKE fl ON f.FILM_ID = fl.FILM_ID \n" +
                "LEFT JOIN MPA m ON f.MPA_ID = m.MPA_ID\n" +
                "WHERE f.FILM_ID = ?\n" +
                "ORDER BY f.FILM_ID ASC";
        List<Film> films = jdbcTemplate.query(sqlSelectFilm, filmExtractor, id);
        if (films.isEmpty()) {
            return null;
        }
        return films.get(0);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        String sql = "SELECT f.*, m.NAME AS MPA_NAME, fg.GENRE_ID, g.NAME AS GENRE_NAME, fl.USER_ID AS LIKE_USER_ID\n" +
                "FROM ( SELECT COUNT(USER_ID) AS LIKE_USER_COUNT, f.*\n" +
                "FROM FILM_LIKE fl\n" +
                "RIGHT JOIN FILM f ON f.FILM_ID = fl.FILM_ID\n" +
                "GROUP BY f.FILM_ID\n" +
                "ORDER BY LIKE_USER_COUNT DESC\n" +
                "LIMIT ?) AS f\n" +
                "LEFT JOIN FILM_GENRE fg ON f.FILM_ID = fg.FILM_ID\n" +
                "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID\n" +
                "LEFT JOIN FILM_LIKE fl ON f.FILM_ID = fl.FILM_ID\n" +
                "LEFT JOIN MPA m ON f.MPA_ID = m.MPA_ID";

        return jdbcTemplate.query(sql,filmExtractor, count);
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

    private Long makeLustId(ResultSet rs) throws SQLException {
        return rs.getLong("FILM_ID");
    }

    private Long makeLikesId(ResultSet rs) throws SQLException {
        return rs.getLong("USER_ID");
    }


}
