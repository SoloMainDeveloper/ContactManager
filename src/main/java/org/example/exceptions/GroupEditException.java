package org.example.exceptions;

/**
 * Исключение: ошибка редактирования группы
 */
public class GroupEditException extends Exception {
    /**
     * Конструктор
     */
    public GroupEditException(Exception e) {
        super(e);
    }
}
