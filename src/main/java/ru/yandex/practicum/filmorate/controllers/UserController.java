package ru.yandex.practicum.filmorate.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.DuplicateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getUserList() {
        return userService.getUsersList();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) throws DuplicateException {
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getUserFriendList(@PathVariable Integer id) throws DuplicateException {
        return userService.getUsersFrendsList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable Integer id,
                                      @PathVariable Integer otherId) throws DuplicateException {
        return userService.getUsersCommonFriends(id, otherId);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws DuplicateException {
        return userService.addUser(user);
    }

    @PutMapping
    public User updUser(@Valid @RequestBody User user) throws DuplicateException {
        return userService.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public int addFriend(@PathVariable Integer id, @PathVariable Integer friendId) throws DuplicateException {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public int deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) throws DuplicateException {
        return userService.deleteFriend(id, friendId);
    }
}
