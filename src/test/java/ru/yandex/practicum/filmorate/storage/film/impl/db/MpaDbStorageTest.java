package ru.yandex.practicum.filmorate.storage.film.impl.db;

import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    public void getMpa_NumberOfMpaShouldBe_5() {
        List<Mpa> mpa = mpaDbStorage.getMpa();
        assertThat(mpa.size(), Matchers.is(5));
    }

    @Test
    public void getMpaById_MpaNameShouldBe_PG() {
        Mpa mpa = mpaDbStorage.getMpaById(2L);
        assertThat(mpa.getName(), Matchers.is("PG"));
    }
}
