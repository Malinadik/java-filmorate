package ru.yandex.practicum.filmorate.validator;



import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateValidator implements ConstraintValidator<CorrectDate, LocalDate> {
    @Override
    public boolean isValid(LocalDate ld, ConstraintValidatorContext constraintValidatorContext) {
        return ld.isAfter(LocalDate.of(1895, 12, 28));
    }
}
