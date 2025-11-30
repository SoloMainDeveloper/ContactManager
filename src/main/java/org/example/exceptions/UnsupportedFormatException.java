package org.example.exceptions;

/**
 * Исключение: формат не поддерживается
 */
public class UnsupportedFormatException extends Exception {
    /**
     * Конструктор
     */
    public UnsupportedFormatException(String message) {
        super(message);
    }
}
