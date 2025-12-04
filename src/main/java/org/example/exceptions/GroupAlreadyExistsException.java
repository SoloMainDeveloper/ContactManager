package org.example.exceptions;

/**
 * Исключение: группа уже существует
 */
public class GroupAlreadyExistsException extends Exception {
    /**
     * Конструктор
     *
     * @param message сообщение ошибки
     */
    public GroupAlreadyExistsException(String message) {
        super(message);
    }
}
