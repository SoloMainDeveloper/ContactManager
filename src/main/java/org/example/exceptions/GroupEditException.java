package org.example.exceptions;

/**
 * Исключение: ошибка редактирования группы
 */
public class GroupEditException extends Exception {
    public GroupEditException(String message) {
        super(message);
    }
}
