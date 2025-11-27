package com.study.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class HelloJunitTest {

    @DisplayName("Hello JUnit Test")
    @Test
    public void assertionTest() {
        String expected = "Hello, World";
        String actual = "Hello, JUnit";

//        assertEquals(expected, actual);
        assertNotEquals(expected, actual);
    }
}
