package org.example.exceptions;

/**
 * Исключение: контакт уже существует
 */
public class ContactAlreadyExistsException extends Exception {
    /**
     * Конструктор
     *
     * @param message сообщение ошибки
     */
    public ContactAlreadyExistsException(String message) {
        super(message);
    }
}
