package org.example.exceptions;

/**
 * Исключение: некорректные данные при создании фильтра
 */
public class IncorrectFilterDataException extends Exception {
    public IncorrectFilterDataException(String message) {
        super(message);
    }
}
