package org.example.service;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.exceptions.ContactAlreadyExistsException;
import org.example.exceptions.ContactDoesNotExistException;
import org.example.repository.IContactRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Сервис для работы с контактами
 */
@Service
public class ContactService {
    /**
     * Репозиторий для контактов
     */
    private final IContactRepository repository;

    /**
     * Конструктор. Инициализируем repository, создавая подключение к БД
     */
    public ContactService(IContactRepository repository) {
        this.repository = repository;
    }

    /**
     * Попытаться добавить контакт
     *
     * @param chatId идентификатор чата пользователя
     * @param contact добавляемый контакт
     * @throws ContactAlreadyExistsException если контакт уже существует
     */
    public void tryAddContact(Long chatId, Contact contact)
            throws ContactAlreadyExistsException {

        String contactName = contact.getName();
        if (findContactByName(chatId, contactName).isEmpty()) {
            repository.add(contact);
        } else {
            throw new ContactAlreadyExistsException(
                    "Контакт %s уже существует".formatted(contactName));
        }
    }

    /**
     * Найти контакт по идентификатору у данного пользователя
     */
    public Optional<Contact> findContactById(Long chatId, Long contactId) {
        return repository.findContactById(contactId, chatId);
    }

    /**
     * Найти контакт по имени у данного пользователя
     */
    public Optional<Contact> findContactByName(Long chatId, String name) {
        return repository.findContactByName(name, chatId);
    }

    /**
     * Найти контакты по номеру
     *
     * @return контакты в случае успеха, в ином случае пустой List.of().
     */
    public List<Contact> findContactsByNumber(Long chatId, String number) {
        return repository.findContactsByNumber(number, chatId);
    }

    /**
     * Возвращает все контакты, имеющееся у данного пользователя
     */
    public List<Contact> findContactsByChatId(Long chatId) {
        return repository.findContactsByChatId(chatId, "", "");
    }

    /**
     * Возвращает все контакты, имеющееся у данного пользователя с применением фильтрации и сортировки
     */
    public List<Contact> findContactsByChatIdWithFilterAndSorter(
            Long chatId, String filter, String sorter) {
        return repository.findContactsByChatId(chatId, filter, sorter);
    }

    /**
     * Изменяет состояние блокировки контакта на противоположное и вызывает update
     * репозитория для данного контакта
     */
    public void toggleContactBlocked(Contact contact) {
        contact.setBlocked(!contact.isBlocked());
        repository.update(contact.getName(), contact);
    }

    /**
     * Попытаться обновить все поля пользователя, кроме блокировки
     *
     * @param chatId идентификатор чата пользователя
     * @param name   имя контакта
     * @param contact обновляемый контакт
     * @throws ContactDoesNotExistException если контакт не существует
     */
    public void tryUpdateContact(Long chatId, String name, Contact contact)
            throws ContactDoesNotExistException {
        Optional<Contact> foundContact = findContactByName(chatId, name);
        foundContact.orElseThrow(() -> new ContactDoesNotExistException(
                "Контакт %s не существует".formatted(name)));
        repository.update(name, contact);
    }

    /**
     * Удалить контакт по имени
     */
    public void deleteByName(Long chatId, String name) {
        repository.deleteByName(name, chatId);
    }
}
