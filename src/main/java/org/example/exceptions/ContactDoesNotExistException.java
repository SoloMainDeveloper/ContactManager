package org.example.exceptions;

/**
 * Исключение: контакт не существует
 */
public class ContactDoesNotExistException extends Exception {
    public ContactDoesNotExistException(String message) {
        super(message);
    }
}
