package com.study.hemcrest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class HelloHamcrestTest {

    @DisplayName("Hello JUnit Test using hamcrest")
    @Test
    public void assertionTest() {
        String expected = "Hello, JUnit";
        String actual = "Hello, JUnit";


        assertThat(actual, is(equalTo(expected)));
//        assertThat(actual, is(not(equalTo(expected))));
    }
}
