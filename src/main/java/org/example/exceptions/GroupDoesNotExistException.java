package org.example.exceptions;

/**
 * Исключение: группа не существует
 */
public class GroupDoesNotExistException extends Exception {
    /**
     * Конструктор
     */
    public GroupDoesNotExistException(String message) {
        super(message);
    }
}
