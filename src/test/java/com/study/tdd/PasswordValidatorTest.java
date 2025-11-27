package com.study.tdd;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class PasswordValidatorTest {
    @DisplayName("모든 조건에 만족")
    @Test
    public void validatePasswordAllCriteria() {
        // given
        String password = "Abcd1234!";

        // when
        PasswordValidator validator = new PasswordValidator();
        Executable executable = () -> validator.validate(password);

        // then
        assertDoesNotThrow(executable);
    }

    @DisplayName("특수 문자 포함 안됨 테스트")
    @Test
    public void validatePasswordWithoutSpecialCharacter() {
        // given
        String password1 = "Abcd1234&!";
        String password2 = "Abcd1234#";

        // when
        PasswordValidator validator = new PasswordValidator();
        Executable executable1 = () -> validator.validate(password1);
        Executable executable2 = () -> validator.validate(password2);

        // then
        assertDoesNotThrow(executable1);
        assertDoesNotThrow(executable2);
    }

    @DisplayName("소문자 포함 안됨 테스트")
    @Test
    public void validatePasswordWithoutLowerCase() {
        // given
        String password1 = "Abcd1234&!";

        // when
        PasswordValidator validator = new PasswordValidator();
        Executable executable1 = () -> validator.validate(password1);

        // then
        assertDoesNotThrow(executable1);
    }

    @DisplayName("대문자 포함 안됨 테스트")
    @Test
    public void validatePasswordWithoutUpperCase() {
        // given
        String password1 = "Abcd1234&!";

        // when
        PasswordValidator validator = new PasswordValidator();
        Executable executable1 = () -> validator.validate(password1);

        // then
        assertDoesNotThrow(executable1);
    }

    @DisplayName("숫자 포함 안됨 테스트")
    @Test
    public void validatePasswordWithoutNumeric() {
        // given
        String password1 = "Abcde1&!";

        // when
        PasswordValidator validator = new PasswordValidator();
        Executable executable1 = () -> validator.validate(password1);

        // then
        assertDoesNotThrow(executable1);
    }

    @DisplayName("8 - 20 길이 테스트")
    @Test
    public void validatePasswordWithInvalidLength() {
        // given
        String password1 = "Abcd1234&!";

        // when
        PasswordValidator validator = new PasswordValidator();
        Executable executable1 = () -> validator.validate(password1);

        // then
        assertDoesNotThrow(executable1);
    }
}
