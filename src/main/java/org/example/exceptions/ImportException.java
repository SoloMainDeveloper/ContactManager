package org.example.exceptions;

/**
 * Исключение: ошибка импорта
 */
public class ImportException extends Exception {
    /**
     * Конструктор
     */
    public ImportException(String message) {
        super(message);
    }
}
