package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.MpaStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaStorage mpaStorage;

    public List<Mpa> getMpa() {
        return mpaStorage.getMpa();
    }

    public Mpa getMpaById(Long id) {
        Mpa receivedMpa = mpaStorage.getMpaById(id);
        if (receivedMpa != null) {
            return receivedMpa;
        } else {
            log.warn("Ошибка получения Mpa. Mpa с ID " + id + " не найден");
            throw new NotFoundException("Ошибка получения Mpa. Mpa с ID " + id + " не найден");
        }
    }
}
