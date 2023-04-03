package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DuplicateException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    @Autowired
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> getFilmsList() {
        return filmStorage.getFilmsList();
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public Film addFilm(Film film) throws DuplicateException {
        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        }
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film.getGenres() == null) {
            film.setGenres(new ArrayList<>());
        }
        return filmStorage.updateFilm(film);
    }

    public Film likeFilm(Integer id, Integer userId) throws DuplicateException {
        userStorage.getUserById(userId);
        Film film = filmStorage.getFilmById(id);
        if (film.getUserLikes().contains(userId)) {
            throw new DuplicateException("You already liked it!");
        }
        filmStorage.likeFilm(id, userId);
        return film;
    }

    public Film unlikeFilm(Integer id, Integer userId) {
        Film film = filmStorage.getFilmById(id);
        if (!film.getUserLikes().contains(userId)) {
            throw new NotFoundException("User not found!");
        }
        filmStorage.unlikeFilm(id, userId);
        return film;
    }

    public List<Film> getMostPopularFilms(Integer count) {
        return filmStorage.getMostPopularFilms(count);
    }
}
