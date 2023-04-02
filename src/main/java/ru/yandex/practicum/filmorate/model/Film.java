package ru.yandex.practicum.filmorate.model;


import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.CorrectDate;

import javax.validation.constraints.*;
import javax.validation.executable.ValidateOnExecution;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@ValidateOnExecution
public class Film {

    private Integer id;
    @NotEmpty(message = "Empty name!")
    @NotNull
    private String name;
    @Size(min = 2, max = 200, message = "Description should be not empty & less then 200")
    @NotEmpty
    private String description;

    @PastOrPresent(message = "Incorrect date!")
    @CorrectDate
    private LocalDate releaseDate;
    @Positive
    private Long duration;
    private List<Genre> genres;
    private MPARating mpa;
    private final Set<Integer> userLikes = new HashSet<>();

}
