package org.example.response;

import org.example.entity.AppDocument;

import java.util.List;

/**
 * Ответ от бота
 */
public class BotResponse {
    /**
     * Текст сообщения
     */
    private String text;

    /**
     * Документ, приложенный к сообщению
     */
    private AppDocument document;

    /**
     * Текст для кнопок клавиатуры
     */
    private List<String> keyboardText;

    /**
     * Содержит текст для inline-кнопок и callbackData для доп. взаимодействий
     */
    private InlineKeyboardText inlineKeyboardText;

    /**
     * Конструктор по умолчанию
     */
    public BotResponse(){
    }

    /**
     * Конструктор с текстом сообщения
     */
    public BotResponse(String text) {
        this.text = text;
    }

    /**
     * Возвращает текст сообщения
     */
    public String getText() {
        return text;
    }

    /**
     * Устанавливает текст сообщения
     */
    public void setText(String text){
        this.text = text;
    }

    /**
     * Возвращает документ, прилагаемый к ответу бота
     */
    public AppDocument getDocument() {
        return document;
    }

    /**
     * Устанавливает документ, прилагаемый к ответу бота
     */
    public void setDocument(AppDocument document) {
        this.document = document;
    }

    /**
     * Возвращает текст для кнопок клавиатуры
     */
    public List<String> getKeyboardText() {
        return keyboardText;
    }

    /**
     * Устанавливает текст для кнопок клавиатуры
     */
    public void setKeyboardText(List<String> keyboardText) {
        this.keyboardText = keyboardText;
    }

    /**
     * Возвращает inlineKeyboardText
     */
    public InlineKeyboardText getInlineKeyboardText() {
        return inlineKeyboardText;
    }

    /**
     * Устанавливает inlineKeyboardText
     */
    public void setInlineKeyboardText(InlineKeyboardText inlineKeyboardText) {
        this.inlineKeyboardText = inlineKeyboardText;
    }
}
