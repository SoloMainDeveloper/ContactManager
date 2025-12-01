package org.example.exceptions;

/**
 * Исключение: контакт уже существует
 */
public class ContactAlreadyExistsException extends Exception {
    /**
     * Конструктор
     */
    public ContactAlreadyExistsException(String message) {
        super(message);
    }
}
