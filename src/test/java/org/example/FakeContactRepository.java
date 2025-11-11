package org.example;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.repository.ContactRepository;
import org.postgresql.ds.PGSimpleDataSource;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Хранилище контактов. Необходимо для тестов
 */
public class FakeContactRepository extends ContactRepository {
    private final Map<Long, Map<String, Contact>> contacts;

    /**
     * Конструктор
     */
    public FakeContactRepository() {
        super(new PGSimpleDataSource());
        contacts = new LinkedHashMap<>();
    }

    /**
     * Возвращает количество контактов у пользователя с данным chatId
     */
    public int getCurrentContactsSize(Long chatId){
        if(contacts.containsKey(chatId)){
            return contacts.get(chatId).size();
        }
        return 0;
    }

    @Override
    public void add(Contact contact) {
        Long chatId = contact.getChatId();
        if(!contacts.containsKey(chatId)){
            contacts.put(chatId, new LinkedHashMap<>());
        }
        if(contacts.get(chatId).containsKey(contact.getName())){
            throw new RuntimeException();
        }
        contacts.get(chatId).put(contact.getName(), contact);
    }

    @Override
    public Optional<Contact> findContactByName(String name, Long chatId) {
        Map<String, Contact> currentChatIdContacts = contacts.get(chatId);
        return currentChatIdContacts == null ||
                currentChatIdContacts.isEmpty() ||
                currentChatIdContacts.get(name) == null
            ? Optional.empty()
            : Optional.of(currentChatIdContacts.get(name));
    }

    @Override
    public Optional<Contact> findContactByNumber(String number, Long chatId) {
        Map<String, Contact> currentChatIdContacts = contacts.get(chatId);
        if(currentChatIdContacts != null) {
            for (Contact current : currentChatIdContacts.values()) {
                if (Objects.equals(current.getPhoneNumber(), number)) {
                    return Optional.of(current);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Contact> findContactsByChatId(Long chatId, String filter, String sorter) {
        Map<String, Contact> userContacts = contacts.getOrDefault(chatId, new HashMap<>());
        List<Contact> contactList = new ArrayList<>(userContacts.values());

        List<Contact> filteredContacts = applyFilter(contactList, filter);

        return applySorter(filteredContacts, sorter);
    }

    /**
     * Применить фильтрацию к списку контактов
     */
    private List<Contact> applyFilter(List<Contact> contacts, String filter) {
        if (filter == null || filter.isEmpty()) {
            return contacts;
        }

        List<Contact> filteredContacts = new ArrayList<>(contacts);
        if (filter.contains("gender")) {
            String genderValue = extractValueFromFilter(filter);
            if (genderValue != null) {
                Gender gender = Gender.valueOf(genderValue);
                filteredContacts = filteredContacts.stream()
                        .filter(contact -> contact.getGender() == gender)
                        .collect(Collectors.toList());
            }
        }

        if (filter.contains("age")) {
            String ageCondition = filter.substring(filter.indexOf("age ") + 4);
            filteredContacts = contacts.stream()
                    .filter(contact -> evaluateAgeCondition(contact.getAge(), ageCondition))
                    .collect(Collectors.toList());
        }

        return filteredContacts;
    }

    /**
     * Извлечь значение из строки фильтра
     */
    private String extractValueFromFilter(String filter) {
        int startIndex = filter.indexOf("gender = '");
        if (startIndex == -1) {
            return null;
        }

        startIndex += "gender = '".length();
        int endIndex = filter.indexOf("'", startIndex);
        if (endIndex == -1) {
            return null;
        }

        return filter.substring(startIndex, endIndex);
    }

    /**
     * Вычислить условие для возраста
     */
    private Boolean evaluateAgeCondition(int age, String condition) {
        if (condition.startsWith(">")) {
            int value = Integer.parseInt(condition.substring(1).trim());
            return age > value;
        } else if (condition.startsWith("<")) {
            int value = Integer.parseInt(condition.substring(1).trim());
            return age < value;
        } else if (condition.startsWith("=")) {
            int value = Integer.parseInt(condition.substring(1).trim());
            return age == value;
        }
        return false;
    }

    /**
     * Применить сортировку к списку контактов
     */
    private List<Contact> applySorter(List<Contact> contacts, String sorter) {
        if (sorter == null || sorter.isEmpty()) {
            return contacts;
        }

        List<Contact> sortedContacts = new ArrayList<>(contacts);
        if (sorter.contains("ORDER BY age ASC")) {
            sortedContacts.sort(Comparator.comparingInt(Contact::getAge));
        }
        else if (sorter.contains("ORDER BY age DESC")) {
            sortedContacts.sort(Comparator.comparingInt(Contact::getAge).reversed());
        }
        else if (sorter.contains("ORDER BY name ASC")) {
            sortedContacts.sort(Comparator.comparing(Contact::getName));
        }
        else if (sorter.contains("ORDER BY name DESC")) {
            sortedContacts.sort(Comparator.comparing(Contact::getName).reversed());
        }
        return sortedContacts;
    }

    @Override
    public void updateBlockField(Contact contact) {
        update(contact);
    }

    @Override
    public void update(Contact contact) {
        Map<String, Contact> currentChatIdContacts = contacts.get(contact.getChatId());
        if(currentChatIdContacts != null) {
            currentChatIdContacts.put(contact.getName(), contact);
        }
    }

    @Override
    public void deleteByName(String name, Long chatId) {
        Map<String, Contact> currentChatIdContacts = contacts.get(chatId);
        if(currentChatIdContacts == null) {
            return;
        }
        currentChatIdContacts.remove(name);
    }
}
