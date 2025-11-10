package org.example.entity;

import org.example.state.State;

/**
 * Пользователь бота
 */
public class User {
    /**
     * Идентификатор чата с пользователем
     */
    private final Long chatId;

    /**
     * Состояние чата
     */
    private final State state = new State();

    /**
     * Конструктор
     */
    public User(Long chatId) {
        this.chatId = chatId;
    }

    /**
     * Возвращает chatId
     */
    public Long getChatId() {
        return chatId;
    }

    /**
     * Возвращает state
     */
    public State getState() {
        return state;
    }
}
