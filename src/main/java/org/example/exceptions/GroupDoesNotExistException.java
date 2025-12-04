package org.example.exceptions;

/**
 * Исключение: группа не существует
 */
public class GroupDoesNotExistException extends Exception {
    /**
     * Конструктор
     *
     * @param message сообщение ошибки
     */
    public GroupDoesNotExistException(String message) {
        super(message);
    }
}
