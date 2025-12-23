package org.example.constants;

/**
 * Класс, содержащий часто используемые текстовые ответы.
 * Вячеслав Юрьевич, константы Вы нам тут разрешили
 */
public class ReplyConstants {
    /**
     * Приватный конструктор, чтобы не создавали экземпляр класса
     */
    private ReplyConstants() {
    }

    /**
     * Ответ, когда бот не понял команду пользователя
     */
    public static final String UNKNOWN_COMMAND = "Я не понимаю эту команду.";

    /**
     * Ответ, когда пользователь возвращается назад
     */
    public static final String COME_BACK = "Вы вернулись назад";

    public static final String NOT_SPECIFIED = "Не указан";

    public static final String BLOCKED = "Заблокирован";

    public static final String NOT_BLOCKED = "Не заблокирован";

    public static final String INCORRECT_DATA = "Некорректные данные";
}
