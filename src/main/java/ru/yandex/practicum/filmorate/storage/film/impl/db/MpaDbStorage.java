package ru.yandex.practicum.filmorate.storage.film.impl.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Component
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa addMpa(Mpa mpa) {
        String sqlInsertGenre = "INSERT INTO MPA (NAME)\n" +
                "VALUES (?)";
        jdbcTemplate.update(sqlInsertGenre, mpa.getName());
        return getMpaById(mpa.getId());
    }

    @Override
    public List<Mpa> getMpa() {
        String sqlSelectGenre = "SELECT * FROM MPA";
        List<Mpa> mpa = jdbcTemplate.query(sqlSelectGenre, (rs, rowNum) -> makeMpa(rs));
        if (mpa.isEmpty()) {
            return Collections.emptyList();
        }
        return mpa;
    }

    @Override
    public Mpa getMpaById(Long MpaId) {
        String sqlSelectMpa = "SELECT * FROM MPA WHERE MPA_ID = ?";
        List<Mpa> mpa = jdbcTemplate.query(sqlSelectMpa, (rs, rowNum) -> makeMpa(rs), MpaId);
        if (mpa.isEmpty()) {
            return null;
        }
        return mpa.get(0);
    }

    private Mpa makeMpa(ResultSet res) throws SQLException {
        Long id = res.getLong("MPA_ID");
        String name = res.getString("NAME");
        return new Mpa(id, name);
    }
}
