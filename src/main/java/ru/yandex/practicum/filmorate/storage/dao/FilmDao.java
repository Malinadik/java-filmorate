package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDao implements FilmStorage {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MpaDao mpaDao;
    @Autowired
    private GenreDao genreDao;

    @Override
    public List<Film> getFilmsList() {
        List<Film> films = new ArrayList<>();
        String sqlQuery = "select * from FILM";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sqlQuery);
        String getLikes = "select USER_ID from USER_LIKES where FILM_ID = ?";
        while (filmRows.next()) {
            Film film = Film.builder().id(filmRows.getInt("id"))
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .duration(filmRows.getLong("duration"))
                    .releaseDate(filmRows.getDate("releasedate").toLocalDate())
                    .genres(convert(filmRows.getInt("id")))
                    .mpa(new MPARating(filmRows.getInt("MPARating_id"),
                            mpaDao.getMPAbyId(filmRows.getInt("MPARating_id")).getName()))
                    .build();
            film.getUserLikes().addAll(jdbcTemplate.queryForList(getLikes, Integer.class, filmRows.getInt("id")));
            films.add(film);
        }
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from film where id = ?", id);
        String getLikes = "select USER_ID from USER_LIKES where FILM_ID = ?";
        if (filmRows.next()) {
            Film film = Film.builder().id(filmRows.getInt("id"))
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .duration(filmRows.getLong("duration"))
                    .releaseDate(filmRows.getDate("releasedate").toLocalDate())
                    .genres(convert(id))
                    .mpa(new MPARating(filmRows.getInt("MPARating_id"),
                            mpaDao.getMPAbyId(filmRows.getInt("MPARating_id")).getName()))
                    .build();
            film.getUserLikes().addAll(jdbcTemplate.queryForList(getLikes, Integer.class, id));
            return film;
        } else {
            throw new NotFoundException("Film not found!");
        }
    }

    @Override
    public Film addFilm(Film film) {
        String sqlQuery = "insert into film(name, description, duration, releasedate, mparating_id) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery,
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setInt(3, film.getDuration().intValue());
            statement.setDate(4, Date.valueOf(film.getReleaseDate()));
            statement.setInt(5, film.getMpa().getId());
            return statement;
        }, kh);
        film.setId(kh.getKey().intValue());
        System.out.println(film.getId());
        if (genreDao.getGenres() != null) {
            convert(film.getId(), film.getGenres());
        }
        if (!film.getUserLikes().isEmpty()) {
            for (int userId : film.getUserLikes())
                likeFilm(film.getId(), userId);
        }

        return getFilmById(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        System.out.println(film.getGenres());
        Film film1 = getFilmById(film.getId());
        String sqlQuery = "update FILM " +
                "set NAME = ?, DESCRIPTION = ?, RELEASEDATE = ?, DURATION = ?, MPARATING_ID = ? " +
                "where ID = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        convert(film.getId(), film.getGenres());

        if (!film.getUserLikes().isEmpty()) {
            for (int userId : film.getUserLikes()) {
                likeFilm(film.getId(), userId);
            }
        }
        return getFilmById(film.getId());
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        String sqlGetPopular = "SELECT f.ID, NAME, DESCRIPTION,DURATION,RELEASEDATE,MPARATING_ID,COUNT(UL.USER_ID) " +
                "FROM FILM f " +
                "INNER JOIN USER_LIKES ul ON f.ID =UL.FILM_ID " +
                "GROUP BY F.ID " +
                "ORDER BY COUNT(ul.USER_ID) DESC " +
                "LIMIT ?";
        List<Film> popular = List.copyOf(jdbcTemplate.query(sqlGetPopular, (rs, rowNum) -> convert(rs), count));
        if (popular.isEmpty()) {
            popular = List.copyOf(getFilmsList());
        }
        return popular;
    }

    public Film likeFilm(Integer id, Integer userId) {
        Film film = getFilmById(id);
        String sqlCheckFilm = "select * from USER_LIKES where film_id = ? and user_id = ?";
        SqlRowSet checkFilm = jdbcTemplate.queryForRowSet(sqlCheckFilm, id, userId);
        if (!checkFilm.next()) {
            String sqlInsertLikes = "insert into USER_LIKES (film_id, user_id) " +
                    "values (?, ?)";
            jdbcTemplate.update(sqlInsertLikes, id, userId);
        }
        film.getUserLikes().add(id);
        return film;
    }

    public Film unlikeFilm(Integer id, Integer userId) {
        Film film = getFilmById(id);
        String deleteLikes = "delete from USER_LIKES where FILM_ID = ? and USER_ID = ? ";
        jdbcTemplate.update(deleteLikes, id, userId);
        film.getUserLikes().remove(id);
        return film;
    }

    private Film convert(ResultSet rs) throws SQLException {
        String getLikes = "select USER_ID from USER_LIKES where FILM_ID = ?";
        Film film = Film.builder().id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .duration(rs.getLong("duration"))
                .releaseDate(rs.getDate("releasedate").toLocalDate())
                .genres(convert(rs.getInt("id")))
                .mpa(new MPARating(rs.getInt("MPARating_id"),
                        mpaDao.getMPAbyId(rs.getInt("MPARating_id")).getName()))
                .build();
        film.getUserLikes().addAll(jdbcTemplate.queryForList(getLikes, Integer.class, film.getId()));
        return film;
    }

    private List<Genre> convert(Integer filmId) {
        List<Genre> genres = new ArrayList<>();
        String sqlGetGenres = "select GENRE_ID from FILMS_GENRES where FILM_ID = ?";
        List<Integer> genresId = List.copyOf(jdbcTemplate.queryForList(sqlGetGenres, Integer.class, filmId));
        if (genresId.isEmpty()) {
            return genres;
        }
        for (Integer gID : genresId) {
            Genre genre = new Genre(gID, genreDao.getGenreById(gID).getName());
            if (!genres.contains(genre)) {
                genres.add(genre);
            }
        }
        return genres;
    }

    private void convert(Integer filmId, List<Genre> genres) {
        String sqlInsertGenres = "insert into FILMS_GENRES(film_id, genre_id) " +
                "values (?, ?)";
        String sqlDropFilmGenres = "delete from FILMS_GENRES where FILM_ID = ?";
        jdbcTemplate.update(sqlDropFilmGenres, filmId);
        if (!genres.isEmpty()) {
            for (Genre genre : genres) {
                jdbcTemplate.update(sqlInsertGenres, filmId, genre.getId());
            }
        }
    }
}
