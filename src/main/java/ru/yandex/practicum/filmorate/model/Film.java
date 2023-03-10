package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.CorrectDate;

import javax.validation.constraints.*;
import javax.validation.executable.ValidateOnExecution;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@ValidateOnExecution
public class Film {

    private Integer id;

    @NotEmpty(message = "Empty name!")
    @NotNull
    private String name;

    @Size(min = 0, max = 200, message = "The maximum length of the description is 200 characters")
    @NotEmpty
    private String description;

    @PastOrPresent(message = "Incorrect date!")
    @CorrectDate
    private LocalDate releaseDate;

    @Positive
    private Long duration;
}
