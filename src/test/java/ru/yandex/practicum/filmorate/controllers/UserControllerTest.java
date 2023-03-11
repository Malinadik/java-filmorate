package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.model.User;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    UserControllerTest() throws URISyntaxException {
    }

    @Autowired
    private MockMvc mvc;

    UserController uc = new UserController();
    @MockBean
    UserController service;

    URI uri = new URI("http://localhost:8080/users");
    JsonMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();


    @Test
    void getUserList() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/users")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void addAndPutUser_Controller() throws Exception {
        User user = User.builder().id(1)
                .email("test@test.ru")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2000, 2, 24))
                .build();
        String json = objectMapper.writeValueAsString(user);

        mvc.perform(post(uri).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        mvc.perform(put(uri).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
    }

    @Test
    void addAndPutUser_ControllerBadStatus_BadEmail() throws Exception {
        User user2 = User.builder().id(2)
                .email("test- @")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(2000, 2, 24))
                .build();
        String errJson = objectMapper.writeValueAsString(user2);

        mvc.perform(post(uri).content(errJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mvc.perform(put(uri).content(errJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addAndPutUser_ControllerBadStatus_BadLogin() throws Exception {
        User user3 = User.builder().id(3)
                .email("test@test.ru")
                .login("")
                .name("test")
                .birthday(LocalDate.of(2000, 2, 24))
                .build();
        String errJson1 = objectMapper.writeValueAsString(user3);

        mvc.perform(post(uri).content(errJson1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mvc.perform(put(uri).content(errJson1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addAndPutUser_ControllerOKStatus_EmptyName() throws Exception {
        User user4 = User.builder().id(4)
                .email("test@test.ru")
                .login("test")
                .birthday(LocalDate.of(2000, 2, 24))
                .build();
        String errJson2 = objectMapper.writeValueAsString(user4);

        mvc.perform(post(uri).content(errJson2).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mvc.perform(put(uri).content(errJson2).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addAndPutUser_ControllerBadStatus_FutureBirthday() throws Exception {
        User user5 = User.builder().id(5)
                .email("test@test.ru")
                .login("test")
                .name("test")
                .birthday(LocalDate.of(3000, 2, 24))
                .build();
        String errJson3 = objectMapper.writeValueAsString(user5);

        mvc.perform(post(uri).content(errJson3).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mvc.perform(put(uri).content(errJson3).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addAndPut_ToMap() {
        User user = User.builder().id(1)
                .email("test@test.ru")
                .login("test")
                .name("Test")
                .birthday(LocalDate.of(2000, 2, 24))
                .build();
        User user2 = User.builder().id(1)
                .email("test@test.ru")
                .login("test")
                .name("Test1")
                .birthday(LocalDate.of(2000, 2, 24))
                .build();
        User user3 = User.builder().id(1)
                .email("test@test.ru")
                .login("test")
                .name("TestTest")
                .birthday(LocalDate.of(2000, 2, 24))
                .build();
        User user4 = User.builder().id(1)
                .email("test@test.ru")
                .login("testLog")
                .birthday(LocalDate.of(2000, 2, 24))
                .build();

        uc.addUser(user);
        Assertions.assertEquals("Test", uc.getUserList().get(0).getName());
        uc.addUser(user2);
        Assertions.assertEquals("Test1", uc.getUserList().get(1).getName());
        uc.updUser(user3);
        Assertions.assertEquals("TestTest", uc.getUserList().get(0).getName());
        uc.addUser(user4);
        Assertions.assertEquals("testLog", uc.getUserList().get(2).getName());
    }
}