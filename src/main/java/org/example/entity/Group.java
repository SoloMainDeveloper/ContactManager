package org.example.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Группа контактов
 */
public class Group {
    /**
     * Идентификатор группы. Генерируется в базе данных
     */
    private Long id;

    /**
     * id чата общения пользователя с ботом
     */
    private Long chatId;

    /**
     * Имя группы
     */
    private String name;

    /**
     * Хранилище контактов группы
     */
    private final Map<Long, Contact> contacts = new HashMap<>();

    /**
     * Конструктор по умолчанию
     */
    public Group() {
    }

    /**
     * Конструктор для новой группы, которой ещё не присвоен id от репозитория
     */
    public Group(Long chatId, String name) {
        this(null, chatId, name);
    }

    /**
     * Конструктор с заполнением всех полей для уже существующей группы
     */
    public Group(Long id, Long chatId, String name) {
        this.id = id;
        this.chatId = chatId;
        this.name = name;
    }

    /**
     * Получить уникальный идентификатор группы
     */
    public Long getId() {
        return id;
    }

    /**
     * Установить уникальный идентификатор группы
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Получить уникальный идентификатор чата с ботом
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
     * Получить контакты группы
     */
    public List<Contact> getContacts() {
        return contacts.values().stream().toList();
    }

    /**
     * Добавить контакт в группу
     */
    public void addContact(Contact contact) {
        contacts.put(contact.getId(), contact);
    }

    /**
     * Удалить контакт из группы
     */
    public void removeContact(Contact contact) {
        contacts.remove(contact.getId());
    }
}
