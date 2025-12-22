package org.example.repository;

import org.example.entity.Contact;
import org.example.utils.ContactFilter;
import org.example.utils.ContactOrder;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий контактов
 */
public interface IContactRepository {
    /**
     * Добавить контакт, при этом присвоив ему id
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
     * Найти все контакты по id пользователя
     */
    List<Contact> findContactsByChatId(Long chatId, ContactFilter filter, ContactOrder order);

    /**
     * Найти контакты, хранящиеся в группе по ее id
     */
    List<Contact> findContactsByGroupId(Long groupId);

    /**
     * Обновить контакт
     *
     * @param currentName имя контакта до обновления
     */
    void update(String currentName, Contact contact);

    /**
     * Удалить контакт по имени, соответствующий конкретному пользователю по chatId
     */
    void deleteByName(String name, Long chatId);
}
