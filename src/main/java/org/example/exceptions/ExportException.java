package org.example.exceptions;

/**
 * Исключение: ошибка экспорта
 */
public class ExportException extends RuntimeException {
    /**
     * Конструктор
     */
    public ExportException(String message) {
        super(message);
    }
}
