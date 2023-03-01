package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
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

    public Film(Set<Long> idUsersLike, Long id, String name, String description, LocalDate releaseDate, Duration duration) {
        this.idUsersLike = new HashSet<>();
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public void addLike (Long likeId) {
        idUsersLike.add(likeId);
    }
    public void deleteLike (Long likeId) {
        idUsersLike.remove(likeId);
    }
}
