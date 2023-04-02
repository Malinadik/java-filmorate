package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DuplicateException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.LikesComparator;

import java.util.*;
import java.util.stream.Collectors;

@Component("InMemory")
public class InMemoryFilmStorage implements FilmStorage {
    private final Comparator<Film> likesComparator = new LikesComparator();
    protected final Set<Film> filmsRating = new TreeSet<>(likesComparator);

    private final HashMap<Integer, Film> filmsList = new HashMap<>();
    private int id = 0;

    @Override
    public List<Film> getFilmsList() {
        return new ArrayList<>(filmsList.values());
    }

    @Override
    public Film getFilmById(int id) {
        if (filmsList.containsKey(id)) {
            return filmsList.get(id);
        }
        throw new NotFoundException("Film not found!"); // throw err
    }

    @Override
    public Film addFilm(Film film) throws DuplicateException {
        id += 1;
        film.setId(id);
        if (filmsList.containsValue(film)) {
            throw new DuplicateException("Film already added!");
        }
        filmsList.put(id, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (filmsList.containsKey(film.getId())) {
            filmsList.put(film.getId(), film);
        } else {
            throw new NotFoundException("Film not found!");
        }
        return film;
    }

    @Override
    public Film likeFilm(Integer id, Integer userId) {
        return null;// по прошлым тз, тут их быть не должно, поэтому добавил такие заглушки
    }

    @Override
    public Film unlikeFilm(Integer id, Integer userId) {
        return null;
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        filmsRating.addAll(filmsList.values());
        return filmsRating.stream().limit(count).collect(Collectors.toList());
    }

}
