package ru.yandex.practicum.filmorate.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final HashMap<Integer, Film> filmsList = new HashMap<>();
    private int id = 0;

    @GetMapping
    public List<Film> getFilmsList() {
        return new ArrayList<>(filmsList.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        id += 1;
        filmsList.put(id, film);
        film.setId(id);
        log.info("Movie added.");
        return film;
    }

    @PutMapping
    public Film updFilm(@Valid @RequestBody Film film) {
        if (filmsList.containsKey(film.getId())) {
            filmsList.put(film.getId(), film);
            log.info("Movie update.");
        } else {
            log.warn("Attempt to update a non-existent movie.");
            throw new RuntimeException("User inst registered!");
        }
        return film;
    }

}
