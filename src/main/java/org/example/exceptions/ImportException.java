package org.example.exceptions;

/**
 * Исключение: ошибка импорта
 */
public class ImportException extends Exception {
    /**
     * Конструктор, принимающий сообщение
     */
    public ImportException(String message) {
        super(message);
    }

    /**
     * Конструктор, принимающий {@link IncorrectImportDataException}
     */
    public ImportException(IncorrectImportDataException e) {
        super(e.getMessage());
    }
}
