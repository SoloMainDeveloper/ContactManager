package org.example.entity;

import java.util.Set;

/**
 * Группа контактов
 */
public class Group {
    /**
     * id чата общения пользователя с ботом
     */
    private Long chatId;

    /**
     * Имя группы
     */
    private String name;

    /**
     * Сет имен id-шников внутри группы
     */
    private Set<Long> contactIds;

    /**
     * Конструктор по умолчанию
     */
    public Group() {
    }

    /**
     * Конструктор с заполнением всех полей
     */
    public Group(Long chatId, String name, Set<Long> contactIds) {
        this.chatId = chatId;
        this.name = name;
        this.contactIds = contactIds;
    }

    /**
     * Получить уникальный идентификатор
     */
    public Long getChatId() {
        return chatId;
    }

    /**
     * Установить уникальный идентификатор
     */
    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    /**
     * Получить название группы
     */
    public String getName() {
        return name;
    }

    /**
     * Установить название группы
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Получить id-шники контактов группы
     */
    public Set<Long> getContactIds() {
        return contactIds;
    }

    /**
     * Установить id-шники контактов группы
     */
    public void setContactIds(Set<Long> contactIds) {
        this.contactIds = contactIds;
    }
}
