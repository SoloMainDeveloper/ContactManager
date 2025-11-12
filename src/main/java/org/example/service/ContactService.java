package org.example.service;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.repository.ContactRepository;
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
    private final ContactRepository repository;

    /**
     * Конструктор. Инициализируем repository, создавая подключение к БД
     */
    public ContactService(ContactRepository repository){
        this.repository = repository;
    }

    /**
     * Попытаться добавить контакт, при успешном выполнении возвращается true
     */
    public boolean tryAddContact(Long chatId, Map<String, String> params) {
        try {
            Contact contact = new Contact(chatId,
                    params.get("contactName"),
                    params.getOrDefault("contactNumber", ""),
                    Integer.parseInt(params.getOrDefault("contactAge",
                            String.valueOf(-1))),
                    Gender.fromDisplayName(params.get("contactGender")),
                    false
            );
            repository.add(contact);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Произошла ошибка при попытке добавить контакт: " + e);
            return false;
        }
    }

    /**
     * Найти контакт по имени у данного пользователя
     */
    public Optional<Contact> findContactByName(Long chatId, String name) {
        return repository.findContactByName(name, chatId);
    }

    /**
     * Найти контакт по номеру у данного пользователя
     */
    public Optional<Contact> findContactByNumber(Long chatId, String number) {
        return repository.findContactByNumber(number, chatId);
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
            Long chatId, Map<String, String> params) {
        String filter = "";
        String sorter = "";
        if(params.containsKey("filterByGender")) {
            String gender = Gender.fromDisplayName(params.get("filterByGender")).name();
            filter = " and gender = '" + gender + "'";
        }
        if(params.containsKey("filterByAge")) {
            String ageCondition = params.get("filterByAge");
            filter = " and age " + ageCondition;
        }
        if(params.containsKey("sorter")) {
            String sorterValue = params.get("sorter");
            if(Objects.equals(sorterValue, "В порядке возрастания возраста")) {
                sorter = " ORDER BY age ASC";
            }
            if(Objects.equals(sorterValue, "В порядке убывания возраста")) {
                sorter = " ORDER BY age DESC";
            }
            if(Objects.equals(sorterValue, "В алфавитном порядке имени")) {
                sorter = " ORDER BY name ASC";
            }
            if(Objects.equals(sorterValue, "В обратном алфавитному порядку имени")) {
                sorter = " ORDER BY name DESC";
            }
        }
        return repository.findContactsByChatId(chatId, filter, sorter);
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
    public Boolean tryUpdateContact(Long chatId, String name, Map<String, String> params) {
        try {
            Contact oldContact = findContactByName(chatId, name).orElseThrow();
            if(params.containsKey("contactName")) {
                oldContact.setName(params.get("contactName"));
            }
            if(params.containsKey("contactNumber")) {
                oldContact.setPhoneNumber(params.get("contactNumber"));
            }
            if(params.containsKey("contactAge")) {
                oldContact.setAge(Integer.parseInt(params.get("contactAge")));
            }
            if(params.containsKey("contactGender")) {
                oldContact.setGender(Gender.fromDisplayName(params.get("contactGender")));
            }
            repository.update(oldContact);
            return true;
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Произошла ошибка при попытке изменить контакт: " + e);
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
