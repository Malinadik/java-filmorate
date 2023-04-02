package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.DuplicateException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {

    List<User> getUsersList();

    User getUserById(Integer id) throws DuplicateException;

    User addUser(User user) throws DuplicateException;

    Set<User> getCommonFriends(int id, int friendId);

    User updateUser(User user) throws DuplicateException;

    int addFriend(Integer id, Integer friendId) throws DuplicateException;

    int deleteFriend(Integer id, Integer friendId);

}
