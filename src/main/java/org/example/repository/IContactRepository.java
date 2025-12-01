package org.example.repository;

import org.example.entity.Contact;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий контактов
 */
public interface IContactRepository {
    /**
     * Добавить контакт
     */
    void add(Contact contact);

    /**
     * Найти контакт по идентификатору, соответствующий конкретному пользователю по chatId
     */
    Optional<Contact> findContactById(Long contactId, Long chatId);

    /**
     * Найти контакт по имени, соответствующий конкретному пользователю по chatId
     */
    Optional<Contact> findContactByName(String name, Long chatId);

    /**
     * Найти контакты по номеру, соответствующие конкретному пользователю по chatId
     */
    List<Contact> findContactsByNumber(String number, Long chatId);

    /**
     * Найти контакты по id пользователя с фильтрацией и сортировкой при необходимости
     */
    List<Contact> findContactsByChatId(Long chatId, String filter, String sorter);

    /**
     * Обновить контакт
     */
    void update(Contact contact);

    /**
     * Удалить контакт по имени, соответствующий конкретному пользователю по chatId
     */
    void deleteByName(String name, Long chatId);
}
