package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.impl.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.impl.db.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.film.impl.db.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.db.UserDbStorage;


import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmDbStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreDbStorage genreDbStorage;
    User user;
    Film film;

    @BeforeEach
    void init() {
        User tempUser = new User(null, "mike@mail.ru", "Login", "Mike", LocalDate.of(2000, 12, 27));
        user = userStorage.addUser(tempUser);
        Film tempFilm = new Film(null, "2012", "Фильм катастрофа", LocalDate.of(2022, 12, 27), Duration.ofHours(1), new ArrayList<>(List.of(new Genre(1L, "Комедия"))), new Mpa(1L, "PG"));
        film = filmDbStorage.addFilm(tempFilm);
    }

    @Test
    public void testAddUser() {
        User userInStorage = userStorage.addUser(user);
        assertThat(userInStorage.getId(), Matchers.greaterThan(1L));
    }

    @Test
    public void testFindUserById() {
        User user = userStorage.getUserById(1L);
        assertThat(user.getName(), Matchers.is("Mike"));
    }

    @Test
    public void testUserUpdate() {
        user.setName("Misha");
        userStorage.updateUser(user);
        User userInStorage = userStorage.getUserById(user.getId());
        assertThat(userInStorage.getName(), Matchers.is("Misha"));
    }

    @Test
    public void testGetUsers() {
        List<User> users = userStorage.getUsers();
        assertThat(users.size(), Matchers.greaterThan(0));
    }

    @Test
    public void testAddFriendUser() {
        User friend = new User(null, "mike@mail.ru", "Login", "Friend", LocalDate.of(2000, 12, 27));
        User friendInStorage = userStorage.addUser(friend);
        userStorage.addFriend(user.getId(), friendInStorage.getId());
        Set<User> users = userStorage.getFriends(user.getId());
        assertThat(users.size(), Matchers.greaterThan(0));
    }

    @Test
    public void testGetFriendsUser() {
        User friend = user;
        friend = userStorage.addUser(friend);
        userStorage.addFriend(user.getId(), friend.getId());
        Set<User> users = userStorage.getFriends(user.getId());
        assertThat(users.size(), Matchers.greaterThan(0));
    }

    @Test
    public void testRemoveFriend() {
        User friend = user;
        friend = userStorage.addUser(friend);
        userStorage.addFriend(user.getId(), friend.getId());
        Set<User> users = userStorage.getFriends(user.getId());
        userStorage.deleteFriend(user.getId(), friend.getId());
        Set<User> users2 = userStorage.getFriends(user.getId());
        assertThat(users.size(), Matchers.greaterThan(users2.size()));
    }

    @Test
    public void testAddFilm() {
        Film filmInStorage = filmDbStorage.addFilm(film);
        assertThat(filmInStorage.getId(), Matchers.greaterThan(1L));
    }

    @Test
    public void testFindFilmById() {
        Film filmInStorage = filmDbStorage.getFilmById(1L);
        assertThat(filmInStorage.getName(), Matchers.is("2012"));
    }

    @Test
    public void testFilmUpdate() {
        film.setName("2011");
        filmDbStorage.updateFilm(film);
        Film filmInStorage = filmDbStorage.getFilmById(film.getId());
        assertThat(filmInStorage.getName(), Matchers.is("2011"));
    }

    @Test
    public void testGetFilms() {
        List<Film> films = filmDbStorage.getFilms();
        assertThat(films.size(), Matchers.greaterThan(0));
    }

    @Test
    public void testAddLikeFilm() {
        filmDbStorage.addLike(film.getId(), user.getId());
        assertThat(filmDbStorage.getFilmLikes(film.getId()).size(), Matchers.greaterThan(0));
    }

    @Test
    public void testDeleteLikeFilm() {
        filmDbStorage.addLike(film.getId(), user.getId());
        filmDbStorage.deleteLike(film.getId(), user.getId());
        assertThat(filmDbStorage.getFilmLikes(film.getId()).size(), Matchers.lessThan(1));
    }

    @Test
    public void testGetPopularFilm() {
        filmDbStorage.addLike(film.getId(), user.getId());
        List<Film> films = filmDbStorage.getPopularFilms(10);
        assertThat(films.size(), Matchers.greaterThan(0));
    }

    @Test
    public void testGetMpa() {
        List<Mpa> mpa = mpaDbStorage.getMpa();
        assertThat(mpa.size(), Matchers.is(5));
    }

    @Test
    public void testGetMpaById() {
        Mpa mpa = mpaDbStorage.getMpaById(2L);
        assertThat(mpa.getName(), Matchers.is("PG"));
    }

    @Test
    public void testGetGenre() {
        List<Genre> genre = genreDbStorage.getGenre();
        assertThat(genre.size(), Matchers.is(6));
    }

    @Test
    public void testGetGenreByID() {
        Genre genre = genreDbStorage.getGenreById(2L);
        assertThat(genre.getName(), Matchers.is("Драма"));
    }

    @Test
    public void testFilmGenreByID() {
        List<Genre> genre = genreDbStorage.getGenre();
        film.setGenres(genre);
        Film filmInStorage = filmDbStorage.updateFilm(film);
        assertThat(filmInStorage.getGenres().size(), Matchers.is(6));
    }
}
