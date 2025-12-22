package org.example.exceptions;

/**
 * Исключение: группа уже существует
 */
public class GroupAlreadyExistsException extends Exception {
    public GroupAlreadyExistsException(String message) {
        super(message);
    }
}
