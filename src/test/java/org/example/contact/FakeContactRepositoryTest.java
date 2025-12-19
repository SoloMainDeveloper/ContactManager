package org.example.contact;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.utils.ContactFilter;
import org.example.utils.ContactOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Тестируем {@link FakeContactRepository#findContactsByChatId} (потому что он сложный)
 */
class FakeContactRepositoryTest {
    /**
     * Фейк-контакт репозиторий
     */
    private FakeContactRepository fakeContactRepository;

    /**
     * ChatId пользователя
     */
    private static final Long chatId = 426L;

    /**
     * Список контактов для тестов
     */
    private static final List<Contact> contacts = new ArrayList<>();

    /**
     * Создаём контакты для дальнейшего использования
     */
    @BeforeAll
    static void setupContacts(){
        Contact contact1 = new Contact(
                1L, chatId, "Юлия", "9543", 34, Gender.FEMALE, false);
        Contact contact2 = new Contact(
                2L, chatId, "Олег", "12345", 45, Gender.MALE, false);
        Contact contact3 = new Contact(
                3L, chatId, "Виктор", "1236", 28, Gender.MALE, false);

        contacts.add(contact1);
        contacts.add(contact2);
        contacts.add(contact3);
    }

    /**
     * Инициализируем и заполняем фейк-контакт репозиторий
     */
    @BeforeEach
    void setupFakeContactRepository() {
        fakeContactRepository = new FakeContactRepository();
        for(Contact contact : contacts){
            fakeContactRepository.add(contact);
        }
    }

    /**
     * Тестируем поиск контактов с сортировкой по имени
     */
    @Test
    void findContactsByChatIdWithSortByName() {
        List<Contact> contactsFound1 = fakeContactRepository.findContactsByChatId(
                chatId, ContactFilter.none(), new ContactOrder(
                        ContactOrder.OrderProperty.NAME, ContactOrder.Direction.ASC));
        Assertions.assertEquals("Виктор", contactsFound1.get(0).getName());
        Assertions.assertEquals("Олег", contactsFound1.get(1).getName());
        Assertions.assertEquals("Юлия", contactsFound1.get(2).getName());

        List<Contact> contactsFound2 = fakeContactRepository.findContactsByChatId(
                chatId, ContactFilter.none(), new ContactOrder(
                        ContactOrder.OrderProperty.NAME, ContactOrder.Direction.DESC));
        Assertions.assertEquals("Юлия", contactsFound2.get(0).getName());
        Assertions.assertEquals("Олег", contactsFound2.get(1).getName());
        Assertions.assertEquals("Виктор", contactsFound2.get(2).getName());
    }

    /**
     * Тестируем поиск контактов с сортировкой по возрасту
     */
    @Test
    void findContactsByChatIdWithSortByAge() {
        List<Contact> contactsFound1 = fakeContactRepository.findContactsByChatId(
                chatId, ContactFilter.none(), new ContactOrder(
                        ContactOrder.OrderProperty.AGE, ContactOrder.Direction.ASC));
        Assertions.assertEquals("Виктор", contactsFound1.get(0).getName());
        Assertions.assertEquals("Юлия", contactsFound1.get(1).getName());
        Assertions.assertEquals("Олег", contactsFound1.get(2).getName());

        List<Contact> contactsFound2 = fakeContactRepository.findContactsByChatId(
                chatId, ContactFilter.none(), new ContactOrder(
                        ContactOrder.OrderProperty.AGE, ContactOrder.Direction.DESC));
        Assertions.assertEquals("Олег", contactsFound2.get(0).getName());
        Assertions.assertEquals("Юлия", contactsFound2.get(1).getName());
        Assertions.assertEquals("Виктор", contactsFound2.get(2).getName());
    }

    /**
     * Тестируем поиск контактов с фильтром по полу
     */
    @Test
    void findContactsByChatIdWithFilterByGender() {
        List<Contact> contactsFound1 = fakeContactRepository.findContactsByChatId(
                chatId, new ContactFilter(ContactFilter.FilterProperty.GENDER,
                        ContactFilter.Condition.EQUALS, Gender.FEMALE.name()),
                ContactOrder.none());
        Assertions.assertEquals(1, contactsFound1.size());
        Assertions.assertEquals("Юлия", contactsFound1.getFirst().getName());

        List<Contact> contactsFound2 = fakeContactRepository.findContactsByChatId(
                chatId, new ContactFilter(ContactFilter.FilterProperty.GENDER,
                        ContactFilter.Condition.EQUALS, Gender.MALE.name()),
                ContactOrder.none());
        Assertions.assertEquals(2, contactsFound2.size());
        Assertions.assertEquals("Олег", contactsFound2.getFirst().getName());
        Assertions.assertEquals("Виктор", contactsFound2.getLast().getName());
    }

    /**
     * Тестируем поиск контактов с фильтром по возрасту
     */
    @Test
    void findContactsByChatIdWithFilterByAge() {
        List<Contact> contactsFound1 = fakeContactRepository.findContactsByChatId(
                chatId, new ContactFilter(ContactFilter.FilterProperty.AGE,
                        ContactFilter.Condition.GREATER_THAN, "30"),
                ContactOrder.none());
        Assertions.assertEquals(2, contactsFound1.size());
        Assertions.assertEquals("Юлия", contactsFound1.getFirst().getName());
        Assertions.assertEquals("Олег", contactsFound1.getLast().getName());

        List<Contact> contactsFound2 = fakeContactRepository.findContactsByChatId(
                chatId, new ContactFilter(ContactFilter.FilterProperty.AGE,
                        ContactFilter.Condition.EQUALS, "28"),
                ContactOrder.none());
        Assertions.assertEquals(1, contactsFound2.size());
        Assertions.assertEquals("Виктор", contactsFound2.getFirst().getName());

        List<Contact> contactsFound3 = fakeContactRepository.findContactsByChatId(
                chatId, new ContactFilter(ContactFilter.FilterProperty.AGE,
                        ContactFilter.Condition.LESS_THAN, "35"),
                ContactOrder.none());
        Assertions.assertEquals(2, contactsFound3.size());
        Assertions.assertEquals("Юлия", contactsFound3.getFirst().getName());
        Assertions.assertEquals("Виктор", contactsFound3.getLast().getName());

        List<Contact> contactsFound4 = fakeContactRepository.findContactsByChatId(
                chatId, new ContactFilter(ContactFilter.FilterProperty.AGE,
                        ContactFilter.Condition.LESS_THAN, "20"),
                ContactOrder.none());
        Assertions.assertEquals(0, contactsFound4.size());
    }

    /**
     * Тестируем поиск контактов при комбинации фильтров и сортировок
     */
    @Test
    void findContactsWithFilterAndSorter() {
        List<Contact> contactsFound1 = fakeContactRepository.findContactsByChatId(
                chatId, new ContactFilter(ContactFilter.FilterProperty.AGE,
                        ContactFilter.Condition.GREATER_THAN, "30"),
                new ContactOrder(
                        ContactOrder.OrderProperty.NAME, ContactOrder.Direction.ASC));
        Assertions.assertEquals(2, contactsFound1.size());
        Assertions.assertEquals("Олег", contactsFound1.getFirst().getName());
        Assertions.assertEquals("Юлия", contactsFound1.getLast().getName());

        List<Contact> contactsFound2 = fakeContactRepository.findContactsByChatId(
                chatId, new ContactFilter(ContactFilter.FilterProperty.GENDER,
                        ContactFilter.Condition.EQUALS, Gender.MALE.name()),
                new ContactOrder(
                        ContactOrder.OrderProperty.AGE, ContactOrder.Direction.DESC));
        Assertions.assertEquals(2, contactsFound2.size());
        Assertions.assertEquals("Олег", contactsFound2.getFirst().getName());
        Assertions.assertEquals("Виктор", contactsFound2.getLast().getName());
    }
}