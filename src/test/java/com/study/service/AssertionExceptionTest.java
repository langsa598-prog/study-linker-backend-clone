package com.study.service;

import com.study.service.CryptoCurrency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AssertionExceptionTest {

    @DisplayName("예외 테스트")
//    @DisplayName("throws NullPointerException when map.get()")
    @Test
    public void assertionThrowExceptionTest() {
        assertThrows(NullPointerException.class, () -> getCryptoCurrency("XRP"));
    }

    @DisplayName("does not throw any Exception when map.get()")
    @Test
    public void assertionDoesNotThrowExceptionTest() {
        assertDoesNotThrow(() -> getCryptoCurrency("BTC"));
    }


    private String getCryptoCurrency(String unit) {
        return CryptoCurrency.map.get(unit).toUpperCase();
    }
}