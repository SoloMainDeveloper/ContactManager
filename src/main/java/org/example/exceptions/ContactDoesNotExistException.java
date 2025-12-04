package org.example.exceptions;

/**
 * Исключение: контакт не существует
 */
public class ContactDoesNotExistException extends Exception {
    /**
     * Конструктор
     *
     * @param message сообщение ошибки
     */
    public ContactDoesNotExistException(String message) {
        super(message);
    }
}
