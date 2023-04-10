package ru.yandex.practicum.filmorate.storage.film.impl.db;

import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;

    @Test
    public void getGenre_NumberOfGenreShouldBe_6() {
        List<Genre> genre = genreDbStorage.getGenre();
        assertThat(genre.size(), Matchers.is(6));
    }

    @Test
    public void getGenreById_GenreNameShouldBe_Drama() {
        Genre genre = genreDbStorage.getGenreById(2L);
        assertThat(genre.getName(), Matchers.is("Драма"));
    }
}
