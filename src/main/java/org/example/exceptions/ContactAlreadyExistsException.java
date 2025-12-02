package org.example.exceptions;

/**
 * Исключение: группа уже существует
 */
public class ContactAlreadyExistsException extends Exception {
    /**
     * Конструктор
     */
    public ContactAlreadyExistsException(String message) {
        super(message);
    }
}
