package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DuplicateException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


@Component
public class InMemoryUserStorage implements UserStorage {
    private HashMap<Integer, User> userList = new HashMap<>();
    private Integer id = 0;

    @Override
    public User addUser(User user) {
        id += 1;
        user.setId(id);
        userList.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getUsersList() {
        return new ArrayList<>(userList.values());
    }

    @Override
    public User getUserById(Integer id) {
        if (!userList.containsKey(id)) {
            throw new NotFoundException("User not found!");
        }
        return userList.get(id);
    }

    @Override
    public User updateUser(User user) {
        if (userList.containsKey(user.getId())) {
            userList.put(user.getId(), user);
        } else {
            throw new NotFoundException("User inst registered!");
        }
        return user;
    }

    @Override
    public Set<User> getCommonFriends(int id, int friendId) {
        return null;
    }

    @Override
    public int addFriend(Integer id, Integer friendId) throws DuplicateException {
        return 0; //аналогично классу фильма
    }

    @Override
    public int deleteFriend(Integer id, Integer friendId) {
        return 0;
    }
}

