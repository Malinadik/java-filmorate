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
import ru.yandex.practicum.filmorate.exceptions.DuplicateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {

    FilmControllerTest() throws URISyntaxException {
    }

    @Autowired
    private MockMvc mvc;

    FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
    @MockBean
    FilmController service;

    JsonMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    URI uri = new URI("http://localhost:8080/films");



    @Test
    void getFilmsList() throws Exception {
        this.mvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/films")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    void addFilmAndRefresh_ControllerOKStatus() throws Exception {
        Film film = Film.builder().name("nisi eiusmod")
                .description("adipisicing")
                .releaseDate(LocalDate.of(2022, 2, 13))
                .duration(100L).build();
        String json = objectMapper.writeValueAsString(film);

        mvc.perform(post(uri).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(put(uri).content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();


    }

    @Test
    void addAndRefresh_controllerBadStatus_EmptyName() throws Exception {
        Film filmEr = Film.builder().id(2).name("")
                .description("Test")
                .releaseDate(LocalDate.of(2023, 2, 13))
                .duration(100L).build();
        String errJson = objectMapper.writeValueAsString(filmEr);
        mvc.perform(post(uri).content(errJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(put(uri).content(errJson).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addAndRefresh_controllerBadStatus_EmptyDescription() throws Exception {
        Film filmEr2 = Film.builder().id(3).name("Test")
                .description("")
                .releaseDate(LocalDate.of(2023, 2, 13))
                .duration(100L).build();
        String errJson1 = objectMapper.writeValueAsString(filmEr2);

        mvc.perform(post(uri).content(errJson1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mvc.perform(put(uri).content(errJson1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addAndRefresh_controllerBadStatus_BadDate() throws Exception {
        Film filmEr3 = Film.builder().id(4).name("Test")
                .description("Test")
                .releaseDate(LocalDate.of(1700, 2, 13))
                .duration(100L).build();
        String errJson2 = objectMapper.writeValueAsString(filmEr3);

        mvc.perform(post(uri).content(errJson2).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mvc.perform(put(uri).content(errJson2).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addAndRefresh_controllerBadStatus_NegativeDuration() throws Exception {
        Film filmEr4 = Film.builder().id(5).name("Test")
                .description("Test")
                .releaseDate(LocalDate.of(2023, 2, 13))
                .duration(-100L).build();
        String errJson3 = objectMapper.writeValueAsString(filmEr4);

        mvc.perform(post(uri).content(errJson3).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        System.out.println(service.getFilmsList());
        mvc.perform(put(uri).content(errJson3).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addAndPut_ToMap() throws DuplicateException {
        Film film = Film.builder().name("Test")
                .description("Test")
                .releaseDate(LocalDate.of(2023, 2, 13))
                .duration(100L).build();
        Film film2 = Film.builder().name("Test1")
                .description("Test")
                .releaseDate(LocalDate.of(2023, 2, 13))
                .duration(100L).build();
        Film film3 = Film.builder().name("TestTest").id(1)
                .description("Test")
                .releaseDate(LocalDate.of(2023, 2, 13))
                .duration(100L).build();

        filmController.addFilm(film);
        Assertions.assertEquals("Test", filmController.getFilmsList().get(0).getName());
        filmController.addFilm(film2);
        Assertions.assertEquals("Test1", filmController.getFilmsList().get(1).getName());
        filmController.updFilm(film3);
        Assertions.assertEquals("TestTest", filmController.getFilmsList().get(0).getName());

    }
}

