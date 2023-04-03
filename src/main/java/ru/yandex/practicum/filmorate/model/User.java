package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import javax.validation.executable.ValidateOnExecution;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@ValidateOnExecution
public class User {
    private Integer id;
    @Email(message = "Incorrect Email!")
    private String email;
    @NotBlank(message = "Incorrect login!")
    @Pattern(regexp = "^\\S*$", message = "Incorrect login!")
    private String login;

    private String name;

    @NotNull
    @PastOrPresent(message = "Incorrect date!")
    private LocalDate birthday;

    private final Set<Integer> friends = new HashSet<>();
    private final Set<Integer> unacceptedFriends = new HashSet<>();
}
