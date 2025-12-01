package org.example.response;

import java.util.List;

/**
 * Содержит текст inline-кнопок, а также название операции, которая будет вызвана при
 * нажатии на них.
 *
 * @param inlineText    Текст inline-кнопок
 * @param operationName Название операции
 */
public record InlineKeyboardText(List<String> inlineText, String operationName) {
    /**
     * Показывает нужно ли выводить inline-кнопки
     */
    public boolean isNeeded() {
        return !inlineText.isEmpty() && !operationName.isEmpty();
    }

    /**
     * Возвращает текст inline-кнопок
     */
    @Override
    public List<String> inlineText() {
        return inlineText;
    }

    /**
     * Возвращает название операции
     */
    @Override
    public String operationName() {
        return operationName;
    }
}
