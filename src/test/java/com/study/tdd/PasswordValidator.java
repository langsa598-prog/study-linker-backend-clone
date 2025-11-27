package com.study.tdd;

import java.util.regex.Pattern;

public class PasswordValidator {
    public void validate(String password) {
        if (!Pattern.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?=\\S+$).{8,20}", password)) {
            throw new RuntimeException("Invalid password");
        }
    }
}
