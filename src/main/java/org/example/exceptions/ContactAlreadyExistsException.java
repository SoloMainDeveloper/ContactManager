package org.example.exceptions;

/**
 * Исключение: контакт уже существует
 */
public class ContactAlreadyExistsException extends Exception {
    public ContactAlreadyExistsException(String message) {
        super(message);
    }
}
