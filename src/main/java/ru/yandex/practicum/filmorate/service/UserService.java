package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DuplicateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> getUsersList() {
        return userStorage.getUsersList();
    }

    public User addUser(User user) throws DuplicateException {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) throws DuplicateException {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        return userStorage.updateUser(user);
    }

    public User getUserById(Integer id) throws DuplicateException {
        return userStorage.getUserById(id);
    }

    public List<User> getUsersFrendsList(Integer id) throws DuplicateException {
        User user = getUserById(id);
        List<User> friendList = new ArrayList<>();
        for (Integer friendId : user.getFriends()) {
            friendList.add(getUserById(friendId));
        }
        return friendList;
    }

    public Set<User> getUsersCommonFriends(Integer id, Integer otherId) throws DuplicateException {
        User user0 = getUserById(id);// тоже для проверки, есть ли юзер
        User user1 = getUserById(otherId);
        return userStorage.getCommonFriends(id, otherId);
    }

    public int addFriend(Integer id, Integer friendId) throws DuplicateException {
        if (id == friendId) {
            throw new DuplicateException("User can't add own page to friends!");
        }
        if (getUserById(id).getFriends().contains(friendId)) {
            throw new DuplicateException("You already send a request!");
        }
        getUserById(friendId);
        return userStorage.addFriend(id, friendId);
    }

    public int deleteFriend(Integer id, Integer friendId) throws DuplicateException {
        User user = getUserById(id);
        if (!user.getFriends().contains(friendId)) {
            throw new DuplicateException("Users not friends yet!");
        }
        return userStorage.deleteFriend(id, friendId);
    }

}




