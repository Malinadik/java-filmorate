package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DuplicateException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.*;
import java.util.List;
import java.util.Set;

@Component
@Primary
@RequiredArgsConstructor
public class UserDao implements UserStorage {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getUsersList() {
        String sqlQuery = "select * from USERS";
        return List.copyOf(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> convert(rs)));
    }

    @Override
    public User getUserById(Integer id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERS where id = ?", id);
        String getFriends = "select FRIEND_ID from FRIENDS where USER_ID = ?";
        if (userRows.next()) {
            User user = User.builder().id(userRows.getInt("id"))
                    .email(userRows.getString("email"))
                    .login(userRows.getString("login"))
                    .name(userRows.getString("name"))
                    .birthday(userRows.getDate("birthday").toLocalDate())
                    .build();

            user.getFriends().addAll(jdbcTemplate.queryForList(getFriends, Integer.class, id));
            return user;
        } else {
            throw new NotFoundException("Film not found!");
        }
    }

    @Override
    public User addUser(User user) throws DuplicateException {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        String sqlQuery = "insert into users(email, login, name, birthday)" +
                "values (?, ?, ?, ?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery,
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        }, kh);
        user.setId(kh.getKey().intValue());
        System.out.println(user.getId());
        if (!user.getFriends().isEmpty()) {
            for (Integer friendId : user.getFriends()) {
                addFriend(user.getId(), friendId);
            }
        }
        return user;
    }

    public void deleteUsers() {
        String sqlDelete = "delete from USERS";
        String sqlRestart = "ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 1";
        jdbcTemplate.update(sqlDelete);
        jdbcTemplate.update(sqlRestart);
    }

    @Override
    public User updateUser(User user) throws DuplicateException {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        User userById = getUserById(user.getId());
        String sqlQuery = "update USERS set email = ?, login = ?, name = ?, birthday = ? where ID = ?";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );
        if (!user.getFriends().isEmpty()) {
            for (Integer friendId : user.getFriends()) {
                addFriend(user.getId(), friendId);
            }
        }
        if (!user.getUnacceptedFriends().isEmpty()) {
            for (Integer friendId : user.getUnacceptedFriends()) {
                deleteFriend(friendId, user.getId());
            }
        }
        return user;
    }

    public Set<User> getCommonFriends(int id, int friendId) {
        String sqlQuery = "select * " +
                "from USERS " +
                "where ID in " +
                "(select friend_id from users u " +
                "join friends f on u.id = f.user_id where u.id = ?) " +
                "and ID in " +
                "(select friend_id " +
                "from users u " +
                "join friends f on u.id = f.user_id where u.id = ?)";
        return Set.copyOf(jdbcTemplate.query(sqlQuery, (rs, rowNum) -> convert(rs), id, friendId));
    }

    public int addFriend(Integer id, Integer friendId) throws DuplicateException {
        String sqlInsert = "insert into FRIENDS (USER_ID, FRIEND_ID, ACCEPTED)" +
                "values(?, ?, ?)";
        jdbcTemplate.update(sqlInsert, id, friendId, false);
        return friendId;
    }

    public int deleteFriend(Integer id, Integer friendId) {
        String sqlDeleteFriend = "delete from FRIENDS where FRIEND_ID = ? and USER_ID = ?";
        jdbcTemplate.update(sqlDeleteFriend, friendId, id);
        return id;
    }


    private User convert(ResultSet rs) throws SQLException {
        User user = User.builder().id(rs.getInt("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
        String getFriends = "select FRIEND_ID from FRIENDS where USER_ID = ?";
        user.getFriends().addAll(jdbcTemplate.queryForList(getFriends, Integer.class, user.getId()));
        return user;
    }
}
