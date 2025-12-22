package org.example.exceptions;

/**
 * Исключение: группа не существует
 */
public class GroupDoesNotExistException extends Exception {
    public GroupDoesNotExistException(String message) {
        super(message);
    }
}
