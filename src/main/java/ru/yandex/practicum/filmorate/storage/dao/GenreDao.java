package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class GenreDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Genre> getGenres() {
        String sqlGetAll = "select ID, GENRE_NAME from GENRES";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlGetAll);
        List<Genre> genres = new ArrayList<>();
        while (rs.next()) {
            Genre genre = new Genre(rs.getInt("ID"), rs.getString("GENRE_NAME"));
            genres.add(genre);
        }
        return genres;
    }

    public Genre getGenreById(Integer id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from GENRES where ID = ?", id);
        if (!filmRows.next()) {
            throw new NotFoundException("Genre not find!");
        }
        return new Genre(filmRows.getInt(1), filmRows.getString(2));
    }
}
