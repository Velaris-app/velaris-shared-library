package com.velaris.shared.security.validator;

import lombok.experimental.UtilityClass;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

@UtilityClass
public class PasswordValidator {

    private static final List<UnaryOperator<String>> RULES = List.of(
            p -> p.length() < 8 ? "Password must be at least 8 characters" : null,
            p -> p.chars().noneMatch(Character::isDigit) ? "Must contain at least one digit" : null,
            p -> p.chars().noneMatch(Character::isLetter) ? "Must contain at least one letter" : null,
            p -> p.chars().allMatch(Character::isLetterOrDigit) ? "Must contain at least one special character" : null
    );

    public static List<String> validate(String password) {
        if (password == null) return List.of("Password cannot be null");

        List<String> errors = new ArrayList<>();
        for (UnaryOperator<String> rule : RULES) {
            String error = rule.apply(password);
            if (error != null) errors.add(error);
        }
        return errors;
    }
}