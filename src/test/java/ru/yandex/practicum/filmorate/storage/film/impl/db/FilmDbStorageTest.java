package ru.yandex.practicum.filmorate.storage.film.impl.db;

import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.impl.db.UserDbStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    User user;
    Film film;

    @BeforeEach
    void init() {
        User tempUser = new User(null, "mike@mail.ru", "Login", "Mike", LocalDate.of(2000, 12, 27));
        user = userStorage.addUser(tempUser);
        Film tempFilm = new Film(null, "2012", "Фильм катастрофа", LocalDate.of(2022, 12, 27), Duration.ofHours(1), new Mpa(1L, "PG"));
        film = filmDbStorage.addFilm(tempFilm);
    }

    @Test
    public void addFilm_addedFilmsMustHaveIdGreaterThan_1() {
        Film filmInStorage = filmDbStorage.addFilm(film);
        assertThat(filmInStorage.getId(), Matchers.greaterThan(1L));
    }

    @Test
    public void getFilmById_FilmMustHaveTheName_2012() {
        Film filmInStorage = filmDbStorage.getFilmById(1L);
        assertThat(filmInStorage.getName(), Matchers.is("2012"));
    }

    @Test
    public void updateFilm_FilmMustHaveTheName_2011() {
        film.setName("2011");
        filmDbStorage.updateFilm(film);
        Film filmInStorage = filmDbStorage.getFilmById(film.getId());
        assertThat(filmInStorage.getName(), Matchers.is("2011"));
    }

    @Test
    public void getFilms_NumberOfFilmsInListMustBeGreaterThan_Zero() {
        List<Film> films = filmDbStorage.getFilms();
        assertThat(films.size(), Matchers.greaterThan(0));
    }

    @Test
    public void addLike_NumberOfLikesFilmInListMustBeGreaterThan_Zero() {
        filmDbStorage.addLike(film.getId(), user.getId());
        assertThat(filmDbStorage.getFilmLikes(film.getId()).size(), Matchers.greaterThan(0));
    }

    @Test
    public void deleteLike_NumberOfLikesFilmShouldBeLessThan_1() {
        filmDbStorage.addLike(film.getId(), user.getId());
        filmDbStorage.deleteLike(film.getId(), user.getId());
        assertThat(filmDbStorage.getFilmLikes(film.getId()).size(), Matchers.lessThan(1));
    }

    @Test
    public void getPopularFilms_NumberOfPopularFilmsMustBeGreaterThan_Zero() {
        filmDbStorage.addLike(film.getId(), user.getId());
        List<Film> films = filmDbStorage.getPopularFilms(10);
        assertThat(films.size(), Matchers.greaterThan(0));
    }
}
