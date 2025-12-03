package org.example.exceptions;

/**
 * Исключение: ошибка в данных при импорте
 */
public class IncorrectImportDataException extends Exception {
    /**
     * Конструктор
     */
    public IncorrectImportDataException(String message) {
        super(message);
    }
}
