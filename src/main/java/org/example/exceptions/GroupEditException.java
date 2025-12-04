package org.example.exceptions;

/**
 * Исключение: ошибка редактирования группы
 */
public class GroupEditException extends Exception {
    /**
     * Конструктор
     *
     * @param message сообщение ошибки
     */
    public GroupEditException(String message) {
        super(message);
    }
}
