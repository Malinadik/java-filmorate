package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DuplicateException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> getFilmsList() {
        return filmStorage.getFilmsList();
    }

    public Film getFilmById(Integer id) {
        return filmStorage.getFilmById(id);
    }

    public Film addFilm(Film film) throws DuplicateException {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film likeFilm(Integer id, Integer userId) {
        userStorage.getUserById(userId); // немного костыль, но как мне кажется, самы простой способ проверить, что юзер есть - если его нет, то будет исключние
        Film film = filmStorage.getFilmById(id);
        film.getUserLikes().add(userId);
        filmStorage.updateFilm(film);
        return film;
    }

    public Film unlikeFilm(Integer id, Integer userId) {
        Film film = filmStorage.getFilmById(id);
        if (!film.getUserLikes().contains(userId)) {
            throw new NotFoundException("Film not found!");
        }
        film.getUserLikes().remove(userId);
        filmStorage.updateFilm(film);
        return film;
    }

    public List<Film> getMostPopularFilms(Integer count) {
        return filmStorage.getMostPopularFilms(count);
    }
}
