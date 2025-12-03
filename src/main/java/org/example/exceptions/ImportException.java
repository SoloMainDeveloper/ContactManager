package org.example.exceptions;

/**
 * Исключение: ошибка импорта
 */
public class ImportException extends RuntimeException {
    /**
     * Конструктор
     */
    public ImportException(String message) {
        super(message);
    }
}
