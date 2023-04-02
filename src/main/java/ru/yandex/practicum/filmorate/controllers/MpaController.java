package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.dao.FilmDao;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
@RequiredArgsConstructor
public class MpaController {
    private final FilmDao filmDao;

    @GetMapping
    public List<MPARating> getRatings() {
        return filmDao.getMPAS();
    }

    @GetMapping("/{id}")
    public MPARating getGenreByID(@PathVariable Integer id) {
        return filmDao.getMPAbyId(id);
    }
}
