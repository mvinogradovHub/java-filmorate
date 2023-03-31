package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
public class Film {
    private Set<Long> idUsersLike;
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Duration duration;
    private List<Genre> genres;
    private Mpa mpa;

    public Film(Long id, String name, String description, LocalDate releaseDate, Duration duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(id, film.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
