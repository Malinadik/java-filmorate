package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DuplicateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
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

    public User addUser(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (user.getName() == null || user.getName().equals("")) {
            user.setName(user.getLogin());
        }
        return userStorage.updateUser(user);
    }

    public User getUserById(Integer id) {
        return userStorage.getUserById(id);
    }

    public List<User> getUsersFrendsList(Integer id) {
        User user = getUserById(id);
        List<User> friendList = new ArrayList<>();
        for (Integer friendId : user.getFriends()) {
            friendList.add(getUserById(friendId));
        }
        return friendList;
    }

    public Set<User> getUsersCommonFriends(Integer id, Integer otherId) {
        User user0 = getUserById(id);
        User user1 = getUserById(otherId);
        Set<User> commonFriends = new HashSet<>();
        for (Integer friend : user0.getFriends()) {
            if (user1.getFriends().contains(friend)) {
                commonFriends.add(getUserById(friend));
            }
        }
        return commonFriends;
    }

    public User addFriend(Integer id, Integer friendId) throws DuplicateException {
        if (id == friendId) {
            throw new DuplicateException("User can't add own page to friends!");
        }
        User user = getUserById(id);
        User user1 = getUserById(friendId);
        user.getFriends().add(friendId);
        user1.getFriends().add(id);
        userStorage.updateUser(user);
        userStorage.updateUser(user1);
        return user;
    }

    public User deleteFriend(Integer id, Integer friendId) throws DuplicateException {
        User user = getUserById(id);
        User user1 = getUserById(friendId);
        if (!user.getFriends().contains(friendId) || !user1.getFriends().contains(id)) {
            throw new DuplicateException("Users not friends yet!");
        }
        user.getFriends().remove(friendId);
        user1.getFriends().remove(id);
        userStorage.updateUser(user);
        userStorage.updateUser(user1);
        return user;
    }

}

