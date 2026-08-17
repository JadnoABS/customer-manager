package com.jadno.datum.CustomerManager.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class CPFValidatorTest {

    private CPFValidator cpfValidator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        cpfValidator = new CPFValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"63276284006", "03695141069"})
    @DisplayName("Should return true for valid CPFs")
    void shouldValidateCorrectCpf(String validCpf) {
        assertTrue(cpfValidator.isValid(validCpf, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {"11111111111", "123", "abc45678900", ""})
    @DisplayName("Should return false for invalid CPFs")
    void shouldInvalidateIncorrectCpf(String invalidCpf) {
        assertFalse(cpfValidator.isValid(invalidCpf, context));
    }
}