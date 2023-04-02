package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.DuplicateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmDaoTest {
    private final FilmDao filmStorage;
    private final UserDao userDao;

    @Order(1)
    @Test
    void getAllFilms() {
        filmStorage.addFilm(Film.builder()
                .name("test")
                .description("test")
                .releaseDate(LocalDate.now())
                .duration(100L)
                .genres(List.of(new Genre(1, "Комедия")))
                .mpa(new MPARating(3, "PG-13"))
                .build());
        filmStorage.addFilm(Film.builder()
                .name("test1")
                .description("test")
                .releaseDate(LocalDate.now())
                .duration(100L)
                .genres(List.of(new Genre(1, "Комедия")))
                .mpa(new MPARating(3, "PG-13"))
                .build());
        filmStorage.addFilm(Film.builder()
                .name("test2")
                .description("test")
                .releaseDate(LocalDate.now())
                .duration(100L)
                .genres(List.of(new Genre(1, "Комедия")))
                .mpa(new MPARating(3, "PG-13"))
                .build());

        List<Film> films = filmStorage.getFilmsList();
        assertThat(films.get(0).getName()).isEqualTo("test");
        assertThat(films.get(1).getDescription()).isEqualTo("test");
        assertThat(films.get(2).getId()).isEqualTo(3);
        assertThat(films.size()).isEqualTo(3);
        assertThat(films.get(2).getName()).isEqualTo("test2");
        assertThat(films.get(2).getDescription()).isEqualTo("test");
        assertThat(films.get(2).getReleaseDate()).isNotNull();
        assertThat(films.get(2).getDuration()).isEqualTo(100L);
        assertThat(films.get(2).getGenres()).isEqualTo(List.of(new Genre(1, "Комедия")));
        assertThat(films.get(2).getMpa()).isEqualTo(new MPARating(3, "PG-13"));
    }

    @Order(2)
    @Test
    void updateFilm() {
        filmStorage.addFilm(Film.builder()
                .name("test")
                .description("test")
                .releaseDate(LocalDate.now())
                .duration(100L)
                .genres(List.of(new Genre(1, "Комедия")))
                .mpa(new MPARating(3, "PG-13"))
                .build());
        Film film = filmStorage.getFilmById(1);
        film.setName("TestCheck");
        filmStorage.updateFilm(film);
        assertThat(filmStorage.getFilmById(1).getName()).isEqualTo("TestCheck");
        assertThat(filmStorage.getFilmById(1).getDescription()).isEqualTo("test");
        assertThat(filmStorage.getFilmById(1).getReleaseDate()).isNotNull();
        assertThat(filmStorage.getFilmById(1).getDuration()).isEqualTo(100L);
        assertThat(filmStorage.getFilmById(1).getGenres()).isEqualTo(List.of(new Genre(1, "Комедия")));
        assertThat(filmStorage.getFilmById(1).getMpa()).isEqualTo(new MPARating(3, "PG-13"));
    }

    @Order(3)
    @Test
    void getFilmById() {
        filmStorage.addFilm(Film.builder()
                        .id(4)
                .name("test")
                .description("test")
                .releaseDate(LocalDate.now())
                .duration(100L)
                .genres(List.of(new Genre(1, "Комедия")))
                .mpa(new MPARating(3, "PG-13"))
                .build());
        Film film = filmStorage.getFilmById(4);
        assertThat(film.getName()).isEqualTo("test");
        assertThat(film.getDescription()).isEqualTo("test");
        assertThat(film.getReleaseDate()).isNotNull();
        assertThat(film.getDuration()).isEqualTo(100L);
        assertThat(film.getGenres()).isEqualTo(List.of(new Genre(1, "Комедия")));
        assertThat(film.getMpa()).isEqualTo(new MPARating(3, "PG-13"));
    }

    @Order(4)
    @Test
    void like_and_UnlikeFilm() throws DuplicateException {
        filmStorage.addFilm(Film.builder()
                .name("test")
                .description("test")
                .releaseDate(LocalDate.now())
                .duration(100L)
                .genres(List.of(new Genre(1, "Комедия")))
                .mpa(new MPARating(3, "PG-13"))
                .build());
        userDao.addUser(User.builder()
                .login("Test")
                .birthday(LocalDate.now())
                .name("TEST")
                .email("test@ya.ru")
                .build());

        assertThat(filmStorage.getFilmById(1).getUserLikes().size()).isEqualTo(0);
        filmStorage.likeFilm(1, 1);
        assertThat(filmStorage.getFilmById(1).getUserLikes().size()).isEqualTo(1);
        assertThat(filmStorage.getFilmById(1).getUserLikes().contains(1)).isTrue();
        filmStorage.unlikeFilm(1,1);
        assertThat(filmStorage.getFilmById(1).getUserLikes().size()).isEqualTo(0);
    }


    @Test
    void testGetAllMpa() {
        List<MPARating> mpaRatingList = filmStorage.getMPAS();
        assertThat(mpaRatingList.size()).isEqualTo(5);
    }

    @Test
    public void testGetMpaById() {
        MPARating mpaRating = filmStorage.getMPAbyId(1);
        assertThat(mpaRating).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void testGetAllGenres() {
        List<Genre> genres = filmStorage.getGenres();
        assertThat(genres.size()).isEqualTo(6);
    }

    @Test
    public void testGetGenreById() {
        Genre genre = filmStorage.getGenreById(1);
        assertThat(genre).hasFieldOrPropertyWithValue("id", 1);
    }
}
