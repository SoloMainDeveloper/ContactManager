package org.example.exceptions;

/**
 * Исключение: ошибка в данных при импорте
 */
public class IncorrectImportDataException extends Exception {
    public IncorrectImportDataException(String message) {
        super(message);
    }
}
