package org.example.exceptions;

/**
 * Исключение: ошибка экспорта
 */
public class ExportException extends Exception {
    public ExportException(String message) {
        super(message);
    }
}
