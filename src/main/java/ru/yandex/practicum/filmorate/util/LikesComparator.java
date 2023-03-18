package ru.yandex.practicum.filmorate.util;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;

public class LikesComparator implements Comparator<Film> {
    @Override
    public int compare(Film o1, Film o2) {
        if (o1.getUserLikes().size() < o2.getUserLikes().size()) {
            return 1;
        } else if (o1.getUserLikes().size() == o2.getUserLikes().size()) {
            return 0;
        }
        return -1;
    }
}
