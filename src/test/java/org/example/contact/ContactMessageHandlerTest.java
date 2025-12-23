package org.example.contact;

import org.example.MessageHandler;
import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.operations.*;
import org.example.operations.contact.*;
import org.example.response.BotResponse;
import org.example.service.ContactService;
import org.example.service.StateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

/**
 * Тестируем обработчики сообщений, взаимодействующие с контактами
 */
class ContactMessageHandlerTest {
    /**
     * Фейковый контакт-репозиторий для тестов
     */
    private FakeContactRepository fakeRepository;

    /**
     * Контакт-сервис для тестов
     */
    private ContactService contactService;

    /**
     * Обработчик сообщений
     */
    private MessageHandler handler;

    /**
     * ChatId пользователя
     */
    private static final Long chatId = 123245663L;

    /**
     * Инициализируем фейковый репозиторий, чтобы не работать напрямую с БД.
     * Инициализируем сервис для более детального тестирования внутренностей.
     * Инициализируем MessageHandler.
     */
    @BeforeEach
    public void setup() {
        fakeRepository = new FakeContactRepository();
        contactService = new ContactService(fakeRepository);
        StateService stateService = new StateService();
        handler = new MessageHandler(
                List.of(new AddContactHandler(contactService, stateService),
                        new BlockContactHandler(contactService, stateService),
                        new ContactsMenuHandler(stateService),
                        new CurrentContactMenuHandler(contactService, stateService),
                        new DeleteContactHandler(contactService, stateService),
                        new EditContactHandler(contactService, stateService),
                        new FindContactHandler(contactService, stateService),
                        new GetAllContactsHandler(contactService, stateService),
                        new MainMenuHandler(stateService)),
                stateService);
    }

    /**
     * Тестируем успешное добавление пользователя, у которого заданы все параметры
     */
    @Test
    public void addContactTest() {
        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Добавить");
        handler.handleMessage(chatId, "Олег");
        handler.handleMessage(chatId, "Номер");
        handler.handleMessage(chatId, "1234567890");
        handler.handleMessage(chatId, "Возраст");
        handler.handleMessage(chatId, "42");
        handler.handleMessage(chatId, "Пол");
        handler.handleMessage(chatId, "Мужской");
        BotResponse response = handler.handleMessage(
                chatId, "Сохранить контакт");
        Assertions.assertEquals(
                "Контакт Олег успешно добавлен",
                response.getText()
        );

        Contact contact = contactService
                .findContactByName(chatId, "Олег")
                .orElse(null);
        Assertions.assertNotNull(contact);
        Assertions.assertEquals("Олег", contact.getName());
        Assertions.assertEquals("1234567890", contact.getPhoneNumber());
        Assertions.assertEquals(42, contact.getAge());
        Assertions.assertEquals("Мужской", contact.getGender().getDisplayName());
    }

    /**
     * Неуспешное добавление контакта, так как уже существует контакт с таким именем
     */
    @Test
    public void addContactThatAlreadyExistsTest() {
        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Добавить");
        handler.handleMessage(chatId, "Олег");
        handler.handleMessage(chatId, "Возраст");
        handler.handleMessage(chatId, "20");
        handler.handleMessage(chatId, "Сохранить контакт");
        handler.handleMessage(chatId, "Добавить");
        handler.handleMessage(chatId, "Олег");
        handler.handleMessage(chatId, "Возраст");
        handler.handleMessage(chatId, "22");
        BotResponse response = handler.handleMessage(
                chatId, "Сохранить контакт");
        Assertions.assertEquals(
                "Произошла ошибка при добавлении: Контакт Олег уже существует",
                response.getText());

        Contact contact = contactService
                .findContactByName(chatId, "Олег")
                .orElse(null);
        Assertions.assertNotNull(contact);
        Assertions.assertEquals("Олег", contact.getName());
        Assertions.assertEquals(20, contact.getAge());
    }

    /**
     * Тестируем успешную блокировку контакта
     */
    @Test
    public void blockContactTest() {
        fakeRepository.add(
                new Contact(chatId, "Юлия", "95436475", 34, Gender.FEMALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Найти");
        handler.handleMessage(chatId, "Поиск по имени");
        handler.handleMessage(chatId, "Юлия");
        handler.handleInlineButtonActivated(chatId, "CURRENT_CONTACT_MENU_Юлия");
        BotResponse response1 = handler.handleMessage(chatId, "Блокировать");

        Assertions.assertEquals(
                "Текущий контакт не заблокирован. Вы хотите заблокировать?",
                response1.getText()
        );

        BotResponse response2 = handler.handleMessage(chatId, "Да");
        Assertions.assertEquals(
                "Контакт Юлия успешно заблокирован",
                response2.getText()
        );

        Contact contact = contactService
                .findContactByName(chatId, "Юлия")
                .orElse(null);
        Assertions.assertNotNull(contact);
        Assertions.assertTrue(contact.isBlocked());
    }

    /**
     * Тестируем успешное удаление контакта
     */
    @Test
    public void deleteContactTest() {
        fakeRepository.add(
                new Contact(chatId, "Юлия", "95436575", 34, Gender.FEMALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Найти");
        handler.handleMessage(chatId, "Поиск по имени");
        handler.handleMessage(chatId, "Юлия");
        handler.handleInlineButtonActivated(chatId, "CURRENT_CONTACT_MENU_Юлия");
        handler.handleMessage(chatId, "Удалить");
        BotResponse response = handler.handleMessage(chatId, "Да");

        Assertions.assertEquals("Контакт Юлия успешно удален", response.getText());
        Assertions.assertTrue(contactService.findContactByName(chatId, "Юлия").isEmpty());
    }

    /**
     * Тестируем успешное редактирование параметров контакта
     */
    @Test
    public void editContactTest() {
        fakeRepository.add(
                new Contact(chatId, "Олег", "95436475", 34, Gender.FEMALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Найти");
        handler.handleMessage(chatId, "Поиск по имени");
        handler.handleMessage(chatId, "Олег");

        handler.handleInlineButtonActivated(chatId, "CURRENT_CONTACT_MENU_Олег");
        handler.handleMessage(chatId, "Изменить");
        handler.handleMessage(chatId,"Номер");
        handler.handleMessage(chatId,"+99923333");
        handler.handleMessage(chatId,"Возраст");
        handler.handleMessage(chatId,"43");
        handler.handleMessage(chatId,"Пол");
        handler.handleMessage(chatId,"Мужской");
        BotResponse response = handler.handleMessage(chatId, "Изменить контакт");
        Assertions.assertEquals(
                "Контакт Олег успешно изменен",
                response.getText()
        );

        List<Contact> contactsByNumber = contactService
                .findContactsByNumber(chatId, "1234567890");
        Assertions.assertTrue(contactsByNumber.isEmpty());

        Contact updatedContact = contactService
                .findContactByName(chatId, "Олег")
                .orElse(null);
        Assertions.assertNotNull(updatedContact);
        Assertions.assertEquals("+99923333", updatedContact.getPhoneNumber());
        Assertions.assertEquals(43, updatedContact.getAge());
        Assertions.assertEquals("Мужской",updatedContact.getGender().getDisplayName());
    }

    /**
     * Тестируем успешный поиск контакта по имени
     */
    @Test
    public void findContactByNameTest() {
        fakeRepository.add(
                new Contact(chatId, "Алексей", "9436475", 34, Gender.MALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Найти");
        handler.handleMessage(chatId, "Поиск по имени");
        BotResponse response = handler.handleMessage(chatId, "Алексей");

        Assertions.assertEquals(
                "По имени Алексей контакт успешно найден.",
                response.getText()
        );
        Assertions.assertEquals(
                List.of("Алексей"),
                response.getInlineKeyboardText().inlineText()
        );
        Optional<Contact> contact = contactService.findContactByName(chatId, "Алексей");
        Assertions.assertTrue(contact.isPresent());
    }

    /**
     * Тестируем успешный поиск контакта по номеру
     */
    @Test
    public void findContactByNumberTest(){
        fakeRepository.add(
                new Contact(chatId, "Алексей", "5236790", 34, Gender.MALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Найти");
        handler.handleMessage(chatId, "Поиск по номеру");
        BotResponse response = handler.handleMessage(chatId, "5236790");

        Assertions.assertEquals(
                "По номеру 5236790 контакты успешно найдены.",
                response.getText()
        );
        Assertions.assertEquals(
                List.of("Алексей"),
                response.getInlineKeyboardText().inlineText()
        );
        Contact contact = contactService.findContactsByNumber(chatId, "5236790").getFirst();
        Assertions.assertNotNull(contact);
    }

    /**
     * Тестируем получение всех контактов
     */
    @Test
    public void getAllContactsTest() {
        fakeRepository.add(new Contact(chatId, "Олег", "523678", 34, Gender.MALE, false));
        fakeRepository.add(new Contact(chatId, "Игорь", "86864", 23, Gender.MALE, false));
        fakeRepository.add(new Contact(chatId, "Юлия", "5236", 76, Gender.FEMALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Получить все");

        BotResponse response = handler.handleMessage(chatId, "Получить");
        Assertions.assertEquals("Все контакты", response.getText());
        Assertions.assertEquals(
                List.of("Олег", "Игорь", "Юлия"),
                response.getInlineKeyboardText().inlineText()
        );
    }

    /**
     * Тестируем получение контактов с фильтрацией по полу
     */
    @Test
    public void getAllContactsWithFilterByGenderTest() {
        fakeRepository.add(new Contact(chatId, "Олег", "523678", 34, Gender.MALE, false));
        fakeRepository.add(new Contact(chatId, "Игорь", "86864", 23, Gender.MALE, false));
        fakeRepository.add(new Contact(chatId, "Юлия", "5236", 76, Gender.FEMALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Получить все");
        handler.handleMessage(chatId, "Добавить фильтр");
        handler.handleMessage(chatId, "По полу");
        handler.handleMessage(chatId, "Мужской");
        BotResponse response = handler.handleMessage(chatId, "Нет");
        Assertions.assertEquals(
                "Все контакты с выбранной фильтрацией",
                response.getText()
        );
        Assertions.assertEquals(
                List.of("Олег", "Игорь"),
                response.getInlineKeyboardText().inlineText()
        );
    }

    /**
     * Тестируем получение контактов с фильтрацией по возрасту
     */
    @Test
    public void getAllContactsWithFilterByAgeTest() {
        fakeRepository.add(new Contact(chatId, "Олег", "523678", 34, Gender.MALE, false));
        fakeRepository.add(new Contact(chatId, "Игорь", "86864", 23, Gender.MALE, false));
        fakeRepository.add(new Contact(chatId, "Юлия", "5236", 76, Gender.FEMALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Получить все");
        handler.handleMessage(chatId, "Добавить фильтр");
        handler.handleMessage(chatId, "По возрасту");
        handler.handleMessage(chatId, "23");
        handler.handleMessage(chatId, "> 23");
        BotResponse response = handler.handleMessage(chatId, "Нет");
        Assertions.assertEquals(
                "Все контакты с выбранной фильтрацией",
                response.getText()
        );
        Assertions.assertEquals(
                List.of("Олег", "Юлия"),
                response.getInlineKeyboardText().inlineText()
        );
    }

    /**
     * Тестируем получение контактов с сортировкой в порядке убывания возраста
     */
    @Test
    public void getAllContactsWithSorterByAgeTest() {
        fakeRepository.add(new Contact(chatId, "Олег", "523678", 34, Gender.MALE, false));
        fakeRepository.add(new Contact(chatId, "Игорь", "86864", 23, Gender.MALE, false));
        fakeRepository.add(new Contact(chatId, "Юлия", "5236", 76, Gender.FEMALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Получить все");
        handler.handleMessage(chatId, "Добавить сортировку");
        BotResponse response = handler
                .handleMessage(chatId, "В порядке убывания возраста");
        Assertions.assertEquals(
                "Все контакты с выбранной сортировкой",
                response.getText()
        );
        Assertions.assertEquals(
                List.of("Юлия", "Олег", "Игорь"),
                response.getInlineKeyboardText().inlineText()
        );
    }

    /**
     * Тестируем получение контактов с сортировкой в алфавитном порядке по имени
     */
    @Test
    public void getAllContactsWithSorterByNameTest() {
        fakeRepository.add(new Contact(chatId, "Олег", "523678", 34, Gender.MALE, false));
        fakeRepository.add(new Contact(chatId, "Игорь", "86864", 23, Gender.MALE, false));
        fakeRepository.add(new Contact(chatId, "Юлия", "5236", 76, Gender.FEMALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Получить все");
        handler.handleMessage(chatId, "Добавить сортировку");
        BotResponse response = handler
                .handleMessage(chatId, "В алфавитном порядке имени");
        Assertions.assertEquals(
                "Все контакты с выбранной сортировкой",
                response.getText()
        );
        Assertions.assertEquals(
                List.of("Игорь", "Олег", "Юлия"),
                response.getInlineKeyboardText().inlineText()
        );
    }

    /**
     * Тестируем информацию о пользователе, у которого заданы все параметры
     */
    @Test
    public void getContactFullInfoTest() {
        fakeRepository.add(
                new Contact(chatId, "Олег", "1234567890", 42, Gender.MALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Найти");
        handler.handleMessage(chatId, "Поиск по имени");
        handler.handleMessage(chatId, "Олег");
        handler.handleInlineButtonActivated(chatId, "CURRENT_CONTACT_MENU_Олег");
        BotResponse response = handler.handleMessage(chatId, "Информация");

        Assertions.assertEquals("""
                Имя контакта: Олег
                Номер телефона: 1234567890
                Возраст: 42
                Пол: Мужской
                Не заблокирован
                """, response.getText());
    }

    /**
     * Тестируем информацию о пользователе, у которого задано только имя
     */
    @Test
    public void getContactInfoOnlyNameTest(){
        fakeRepository.add(
                new Contact(chatId, "Олег", "", -1, Gender.NOT_SPECIFIED, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Найти");
        handler.handleMessage(chatId, "Поиск по имени");
        handler.handleMessage(chatId, "Олег");
        handler.handleInlineButtonActivated(chatId, "CURRENT_CONTACT_MENU_Олег");
        BotResponse response = handler.handleMessage(chatId, "Информация");

        Assertions.assertEquals("""
                Имя контакта: Олег
                Номер телефона: Не указан
                Возраст: Не указан
                Пол: Не выбрано
                Не заблокирован
                """, response.getText());
    }
}