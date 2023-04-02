package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.DuplicateException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoTest {
    private final UserDao userStorage;

    @BeforeEach
    void tearDown() {
        userStorage.deleteUsers();
    }

    @Order(1)
    @Test
    public void testGetAllUsers() throws DuplicateException {
        userStorage.addUser(User.builder().login("Test").birthday(LocalDate.now()).name("TEST").email("test@ya.ru").build());
        userStorage.addUser(User.builder().login("Test1").birthday(LocalDate.now()).name("TEST1").email("test1@ya.ru").build());
        userStorage.addUser(User.builder().login("Test2").birthday(LocalDate.now()).name("TEST2").email("test2@ya.ru").build());
        List<User> users = userStorage.getUsersList();
        assertThat(users.size()).isEqualTo(3);
        assertThat(users.get(0).getLogin()).isEqualTo("Test");
        assertThat(users.get(0).getName()).isEqualTo("TEST");
        assertThat(users.get(0).getBirthday()).isNotNull();
        assertThat(users.get(0).getEmail()).isEqualTo("test@ya.ru");
        assertThat(users.get(1).getLogin()).isEqualTo("Test1");
        assertThat(users.get(2).getId()).isEqualTo(3);
    }

    @Order(2)
    @Test
    public void testFindUserById() throws DuplicateException {
        userStorage.addUser(User.builder().login("Test").birthday(LocalDate.now()).name("TEST").email("test@ya.ru").build());
        User user1 = userStorage.getUserById(1);
        assertThat(user1).hasFieldOrPropertyWithValue("id", 1);
        assertThat(user1.getLogin()).isEqualTo("Test");
        assertThat(user1.getBirthday()).isNotNull();
        assertThat(user1.getName()).isEqualTo("TEST");
        assertThat(user1.getEmail()).isEqualTo("test@ya.ru");
    }

    @Order(3)
    @Test
    public void testUserUpdate() throws DuplicateException {
        userStorage.addUser(User.builder().login("Test").birthday(LocalDate.now()).name("TEST").email("test@ya.ru").build());
        User user = userStorage.getUserById(1);
        user.setLogin("TEST1");
        userStorage.updateUser(user);
        assertThat(userStorage.getUserById(1).getLogin()).isEqualTo("TEST1");
    }

    @Order(4)
    @Test
    void add_And_DeleteFriend() throws DuplicateException {
        userStorage.addUser(User.builder().login("Test").birthday(LocalDate.now()).name("TEST").email("test@ya.ru").build());
        userStorage.addUser(User.builder().login("Test1").birthday(LocalDate.now()).name("TEST1").email("test1@ya.ru").build());
        userStorage.addUser(User.builder().login("Test2").birthday(LocalDate.now()).name("TEST2").email("test2@ya.ru").build());
        assertThat(userStorage.getUserById(1).getFriends().size()).isEqualTo(0);
        assertThat(userStorage.getUserById(2).getFriends().size()).isEqualTo(0);
        userStorage.addFriend(1, 2);
        assertThat(userStorage.getUserById(1).getFriends().size()).isEqualTo(1);
        assertThat(userStorage.getUserById(2).getFriends().size()).isEqualTo(0);
        userStorage.addFriend(2, 1);
        assertThat(userStorage.getUserById(1).getFriends().size()).isEqualTo(1);
        assertThat(userStorage.getUserById(2).getFriends().size()).isEqualTo(1);
        userStorage.deleteFriend(2, 1);
        assertThat(userStorage.getUserById(1).getFriends().size()).isEqualTo(1);
        assertThat(userStorage.getUserById(2).getFriends().size()).isEqualTo(0);
        userStorage.deleteFriend(1, 2);
        assertThat(userStorage.getUserById(1).getFriends().size()).isEqualTo(0);
        assertThat(userStorage.getUserById(2).getFriends().size()).isEqualTo(0);
    }

    @Order(5)
    @Test
    void getCommonFriends() throws DuplicateException {
        userStorage.addUser(User.builder().login("Test").birthday(LocalDate.now()).name("TEST").email("test@ya.ru").build());
        userStorage.addUser(User.builder().login("Test1").birthday(LocalDate.now()).name("TEST1").email("test1@ya.ru").build());
        userStorage.addUser(User.builder().login("Test2").birthday(LocalDate.now()).name("TEST2").email("test2@ya.ru").build());
        userStorage.addFriend(1, 3);
        userStorage.addFriend(2, 3);
        userStorage.addFriend(3, 1);
        userStorage.addFriend(3, 2);
        List<User> commonFriends = List.copyOf(userStorage.getCommonFriends(1, 2));
        assertThat(commonFriends.size()).isEqualTo(1);
        assertThat(commonFriends.get(0).getLogin()).isEqualTo("Test2");
        assertThat(commonFriends.get(0).getName()).isEqualTo("TEST2");

    }
}
