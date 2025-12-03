package org.example.exceptions;

/**
 * Исключение: ошибка экспорта
 */
public class ExportException extends Exception {
    /**
     * Конструктор
     */
    public ExportException(String message) {
        super(message);
    }
}
