package com.example.pantera.domain.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidateException;
}
