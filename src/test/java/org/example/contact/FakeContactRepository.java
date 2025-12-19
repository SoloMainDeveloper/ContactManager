package org.example.contact;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.repository.IContactRepository;
import org.example.utils.ContactFilter;
import org.example.utils.ContactOrder;

import java.util.*;

/**
 * Хранилище контактов. Необходимо для тестов
 */
public class FakeContactRepository implements IContactRepository {
    /**
     * Хранилище контактов
     */
    private final Map<Long, Map<String, Contact>> contacts;

    /**
     * Счетчик - мнимая генерация id контакта
     */
    private Long counter;

    public FakeContactRepository() {
        contacts = new LinkedHashMap<>();
        counter = 0L;
    }

    @Override
    public void add(Contact contact) {
        Long chatId = contact.getChatId();
        if(!contacts.containsKey(chatId)){
            contacts.put(chatId, new LinkedHashMap<>());
        }
        Contact contactWithId = new Contact(counter, contact.getChatId(),
                contact.getName(), contact.getPhoneNumber(), contact.getAge(),
                contact.getGender(), contact.isBlocked());
        contacts.get(chatId).put(contact.getName(), contactWithId);
        counter++;
    }

    @Override
    public Optional<Contact> findContactById(Long contactId, Long chatId) {
        Map<String, Contact> currentContacts = contacts.get(chatId);
        return currentContacts.values().stream()
                .filter(contact ->
                        contact.getId() != null && contact.getId().equals(contactId))
                .findFirst();
    }

    @Override
    public Optional<Contact> findContactByName(String name, Long chatId) {
        Map<String, Contact> currentContacts = contacts.get(chatId);
        return currentContacts == null ||
                currentContacts.isEmpty() ||
                currentContacts.get(name) == null
            ? Optional.empty()
            : Optional.of(currentContacts.get(name));
    }

    @Override
    public List<Contact> findContactsByNumber(String number, Long chatId) {
        Map<String, Contact> currentChatIdContacts = contacts.get(chatId);
        List<Contact> contacts = new ArrayList<>();
        if(currentChatIdContacts != null) {
            for (Contact current : currentChatIdContacts.values()) {
                if (Objects.equals(current.getPhoneNumber(), number)) {
                    contacts.add(current);
                }
            }
        }
        return contacts;
    }

    @Override
    public List<Contact> findContactsByChatId(Long chatId, ContactFilter filter, ContactOrder order) {
        Map<String, Contact> userContacts = contacts.getOrDefault(chatId, new HashMap<>());
        List<Contact> contactList = new ArrayList<>(userContacts.values());

        List<Contact> filteredContacts = getContactsAfterFiltering(contactList, filter);

        return getContactsAfterSorting(filteredContacts, order);
    }

    /**
     * Получить контакты после примененной фильтрации
     */
    private List<Contact> getContactsAfterFiltering(List<Contact> contacts, ContactFilter filter) {
        switch (filter.property()) {
            case GENDER -> {
                Gender gender = Gender.valueOf(filter.value());
                return contacts.stream()
                    .filter(contact -> contact.getGender() == gender)
                    .toList();
            }
            case AGE -> {
                return contacts.stream()
                    .filter(contact ->
                        evaluateAgeCondition(contact.getAge(), filter))
                    .toList();
            }
            case null, default -> {
                return contacts;
            }
        }
    }

    /**
     * Вычислить условие для возраста
     */
    private boolean evaluateAgeCondition(int contactAge, ContactFilter filter) {
        int age = Integer.parseInt(filter.value());
        return switch (filter.condition()) {
            case LESS_THAN -> contactAge < age;
            case GREATER_THAN -> contactAge > age;
            case EQUALS -> contactAge == age;
        };
    }

    /**
     * Получить контакты после примененной сортировки
     */
    private List<Contact> getContactsAfterSorting(List<Contact> contacts, ContactOrder order) {
        List<Contact> sortedContacts = new ArrayList<>(contacts);
        if(order.property() == ContactOrder.OrderProperty.AGE) {
            switch (order.direction()) {
                case ASC -> sortedContacts
                    .sort(Comparator.comparingInt(Contact::getAge));
                case DESC -> sortedContacts
                    .sort(Comparator.comparingInt(Contact::getAge).reversed());
            }
        } else if(order.property() == ContactOrder.OrderProperty.NAME) {
            switch (order.direction()) {
                case ASC -> sortedContacts
                    .sort(Comparator.comparing(Contact::getName));
                case DESC -> sortedContacts
                    .sort(Comparator.comparing(Contact::getName).reversed());
            }
        }
        return sortedContacts;
    }

    @Override
    public List<Contact> findContactsByGroupId(Long groupId) {
        return List.of(); // этот метод используется только в GroupRepository и в
        // фейковой реализации он не нужен
    }

    @Override
    public void update(String currentName, Contact contact) {
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
