package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class MpaDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<MPARating> getMPAS() {
        String sqlGetAll = "select ID, RATING_NAME from MPARATING";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sqlGetAll);
        List<MPARating> ratings = new ArrayList<>();
        while (rs.next()) {
            MPARating mpaRating = new MPARating(rs.getInt("ID"), rs.getString("RATING_NAME"));
            ratings.add(mpaRating);
        }
        return ratings;
    }

    public MPARating getMPAbyId(Integer id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from MPARATING where ID = ?", id);
        if (!filmRows.next()) {
            throw new NotFoundException("MPA not find!");
        }
        return new MPARating(filmRows.getInt(1), filmRows.getString(2));
    }
}
