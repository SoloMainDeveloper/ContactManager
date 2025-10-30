package org.example.service;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.repository.ContactRepository;
import org.example.DataBase;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
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

    /**
     * Попытаться добавить контакт, при успешном выполнении возвращается true
     */
    public boolean tryAddContact(Long chatId, HashMap<String, String> params) {
        try {
            Contact contact = new Contact(chatId,
                    params.get("contactName"),
                    params.getOrDefault("contactNumber", ""),
                    Integer.parseInt(params.getOrDefault("contactAge", String.valueOf(-1))),
                    Gender.fromDisplayName(params.get("contactGender")),
                    false
            );
            repository.add(contact);
            return true;
        } catch (Exception exception){
            return false;
        }
    }

    /**
     * Найти контакт по имени
     * @return контакт в случае успеха, в случае неудачи - null.
     */
    public Contact findContactByName(Long chatId, String name) {
        Optional<Contact> contact = repository.findContactByName(name, chatId);
        return contact.orElse(null);
    }

    /**
     * Найти контакт по номеру
     * @return контакт в случае успеха, в случае неудачи - null.
     */
    public Contact findContactByNumber(Long chatId, String number) {
        Optional<Contact> contact = repository.findContactByNumber(number, chatId);
        return contact.orElse(null);
    }

    /**
     * Возвращает все контакты, имеющееся у данного пользователя
     */
    public List<Contact> findContactsByChatId(Long chatId) {
        return repository.findContactsByChatId(chatId);
    }

    /**
     * Обновляет поле блокировки пользователя
     */
    public void updateBlockField(Contact contact) {
        repository.updateBlockField(contact);
    }

    /**
     * Попытаться обновить все поля пользователя, кроме блокировки
     */
    public Boolean tryUpdateContact(HashMap<String, String> params, Contact oldContact) {
        try {
            if(params.containsKey("contactName")) oldContact.setName(params.get("contactName"));
            if(params.containsKey("contactNumber")) oldContact.setPhoneNumber(params.get("contactNumber"));
            if(params.containsKey("contactAge")) oldContact.setAge(Integer.parseInt(params.get("contactAge")));
            if(params.containsKey("contactGender")) oldContact.setGender(Gender.fromDisplayName(params.get("contactGender")));

            repository.update(oldContact);
            return true;
        } catch (Exception exception){
            return false;
        }
    }

    /**
     * Удалить контакт по имени
     */
    public void deleteByName(Long chatId, String name) {
        repository.deleteByName(name, chatId);
    }
}
