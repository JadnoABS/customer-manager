package com.jadno.datum.CustomerManager.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CPFValidator implements ConstraintValidator<CPF, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }

        String cpf = value.replaceAll("\\D", "");

        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) {
            return false;
        }

        return validCheckDigit(cpf, 9) && validCheckDigit(cpf, 10);
    }

    private boolean validCheckDigit(String cpf, int digitPosition) {
        int sum = 0;
        int weight = digitPosition + 1;

        for (int i = 0; i < digitPosition; i++) {
            sum += Character.getNumericValue(cpf.charAt(i)) * weight--;
        }

        int rest = sum % 11;
        int expectedDigit = (rest < 2) ? 0 : 11 - rest;

        return expectedDigit == Character.getNumericValue(cpf.charAt(digitPosition));
    }
}