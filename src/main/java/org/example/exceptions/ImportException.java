package org.example.exceptions;

/**
 * Исключение: ошибка импорта
 */
public class ImportException extends Exception {
    public ImportException(String message) {
        super(message);
    }

    public ImportException(IncorrectImportDataException e) {
        super(e.getMessage());
    }
}
