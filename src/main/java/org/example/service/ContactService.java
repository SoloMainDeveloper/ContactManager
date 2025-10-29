package org.example.service;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.repository.ContactRepository;
import org.example.DataBase;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Optional;

/**
 * Сервис для работы с контактами
 */
public class ContactService {
    private final ContactRepository repository;

    /**
     * Конструктор. Инициализируем repository, создавая подключение к БД
     */
    public ContactService(){
        DataSource dataSource = new DataBase().buildDataSource();
        repository = new ContactRepository(dataSource);
    }

    public boolean tryAddContact(long chatId, HashMap<String, String> params) {
        try {
            Contact contact = new Contact(chatId,
                    params.get("contactName"),
                    params.get("contactNumber"),
                    Integer.parseInt(params.get("contactAge")),
                    Gender.fromDisplayName(params.get("contactGender")));
            repository.add(contact);
            return true;
        } catch (Exception exception){

            return false;
        }
    }

    /**
     * Поиск контакта по имени
     * @return контакт в случае успеха, в случае неудачи - null.
     */
    public Contact findContactByName(long chatId, String name) {
        Optional<Contact> contact = repository.findByName(name, chatId);
        return contact.orElse(null);
    }

    /**
     * Поиск контакта по номеру
     * @return контакт в случае успеха, в случае неудачи - null.
     */
    public Contact findContactByNumber(long chatId, String number) {
        Optional<Contact> contact = repository.findByNumber(number, chatId);
        return contact.orElse(null);
    }

    public void deleteByName(long chatId, String name) {
        repository.deleteByName(name, chatId);
    }
}
