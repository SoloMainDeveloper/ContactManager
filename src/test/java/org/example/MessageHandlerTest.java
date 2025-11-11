package org.example;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.operations.*;
import org.example.repository.StateRepository;
import org.example.response.BotResponse;
import org.example.service.ContactService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

/**
 * Тестируем обработчик сообщений
 */
class MessageHandlerTest {
    /**
     * Фейковый контакт-репозиторий для тестов
     */
    private FakeContactRepository repository;

    /**
     * Контакт-сервис для тестов
     */
    private ContactService contactService;

    /**
     * Сервис состояний
     */
    private StateService stateService;

    /**
     * Обработчик сообщений
     */
    private MessageHandler handler;

    /**
     * ChatId пользователя
     */
    public final Long chatId = 123245663L;

    /**
     * Инициализируем фейковый репозиторий, чтобы не работать напрямую с БД.
     * Инициализируем сервис для более детального тестирования внутренностей.
     * Инициализируем MessageHandler.
     */
    @BeforeEach
    public void setup() {
        repository = new FakeContactRepository();
        contactService = new ContactService(repository);
        stateService = new StateService(new StateRepository());
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
        Assertions.assertEquals(
                Operation.ADD_CONTACT,
                stateService.getOperation(chatId)
        );

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
        Assertions.assertEquals(Operation.CONTACTS_MENU,
                stateService.getOperation(chatId));
        Assertions.assertEquals(1, repository.getCurrentContactsSize(chatId));
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
        Assertions.assertEquals(
                Operation.CONTACTS_MENU,
                stateService.getOperation(chatId)
        );

        handler.handleMessage(chatId, "Добавить");
        Assertions.assertEquals(
                Operation.ADD_CONTACT,
                stateService.getOperation(chatId)
        );
        handler.handleMessage(chatId, "Олег");
        handler.handleMessage(chatId, "Возраст");
        handler.handleMessage(chatId, "22");
        BotResponse response = handler.handleMessage(
                chatId, "Сохранить контакт");
        Assertions.assertEquals(
                "Контакт Олег не был добавлен. Произошла ошибка",
                response.getText());

        Contact contact = contactService
                .findContactByName(chatId, "Олег")
                .orElse(null);
        Assertions.assertNotNull(contact);
        Assertions.assertEquals("Олег", contact.getName());
        Assertions.assertEquals(20, contact.getAge());
        Assertions.assertEquals(Operation.CONTACTS_MENU,
                stateService.getOperation(chatId));
        Assertions.assertEquals(1, repository.getCurrentContactsSize(chatId));
    }

    /**
     * Тестируем успешную блокировку контакта
     */
    @Test
    public void blockContactTest() {
        repository.add(new Contact(chatId, "Юлия", "95436475", 34, Gender.FEMALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Найти");
        handler.handleMessage(chatId, "Поиск по имени");
        handler.handleMessage(chatId, "Юлия");
        handler.handleInlineButtonActivated(chatId, "CURRENT_CONTACT_MENU_Юлия");
        BotResponse response1 = handler.handleMessage(chatId, "Блокировать");
        Assertions.assertEquals(
                Operation.BLOCK_CONTACT,
                stateService.getOperation(chatId)
        );
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
        repository.add(new Contact(chatId, "Юлия", "95436575", 34, Gender.FEMALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Найти");
        handler.handleMessage(chatId, "Поиск по имени");
        handler.handleMessage(chatId, "Юлия");
        handler.handleInlineButtonActivated(chatId, "CURRENT_CONTACT_MENU_Юлия");
        handler.handleMessage(chatId, "Удалить");
        Assertions.assertEquals(
                Operation.DELETE_CONTACT,
                stateService.getOperation(chatId)
        );
        BotResponse response = handler.handleMessage(chatId, "Да");

        Assertions.assertEquals("Контакт Юлия успешно удален", response.getText());
        Assertions.assertTrue(contactService.findContactByName(chatId, "Юлия").isEmpty());
        Assertions.assertEquals(0, repository.getCurrentContactsSize(chatId));
    }

    /**
     * Тестируем успешное редактирование параметров контакта
     */
    @Test
    public void editContactTest() {
        repository.add(new Contact(chatId, "Олег", "95436475", 34, Gender.FEMALE, false));

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

        Optional<Contact> contactByNumber = contactService
                .findContactByNumber(chatId, "1234567890");
        Assertions.assertTrue(contactByNumber.isEmpty());

        Contact updatedContact = contactService
                .findContactByName(chatId, "Олег")
                .orElse(null);
        Assertions.assertNotNull(updatedContact);
        Assertions.assertEquals("+99923333", updatedContact.getPhoneNumber());
        Assertions.assertEquals(43, updatedContact.getAge());
        Assertions.assertEquals("Мужской",updatedContact.getGender().getDisplayName());
        Assertions.assertEquals(1, repository.getCurrentContactsSize(chatId));
    }

    /**
     * Тестируем успешный поиск контакта по имени
     */
    @Test
    public void findContactByNameTest() {
        repository.add(new Contact(chatId, "Алексей", "9436475", 34, Gender.MALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Найти");
        Assertions.assertEquals(
                Operation.FIND_CONTACT,
                stateService.getOperation(chatId)
        );
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
        Assertions.assertEquals(1, repository.getCurrentContactsSize(chatId));
    }

    /**
     * Тестируем успешный поиск контакта по номеру
     */
    @Test
    public void findContactByNumberTest(){
        repository.add(new Contact(chatId, "Алексей", "5236790", 34, Gender.MALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Найти");
        Assertions.assertEquals(
                Operation.FIND_CONTACT,
                stateService.getOperation(chatId)
        );
        handler.handleMessage(chatId, "Поиск по номеру");
        BotResponse response = handler.handleMessage(chatId, "5236790");

        Assertions.assertEquals(
                "По номеру 5236790 контакт успешно найден.",
                response.getText()
        );
        Assertions.assertEquals(
                List.of("Алексей"),
                response.getInlineKeyboardText().inlineText()
        );
        Optional<Contact> contact = contactService.findContactByNumber(chatId, "5236790");
        Assertions.assertTrue(contact.isPresent());
        Assertions.assertEquals(1, repository.getCurrentContactsSize(chatId));
    }

    /**
     * Тестируем получение всех контактов
     */
    @Test
    public void getAllContactsTest() {
        repository.add(new Contact(chatId, "Олег", "52367890", 34, Gender.MALE, false));
        repository.add(new Contact(chatId, "Игорь", "868645435", 23, Gender.MALE, false));
        repository.add(new Contact(chatId, "Юлия", "52367890", 76, Gender.FEMALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Получить все");
        Assertions.assertEquals(
                Operation.GET_ALL_CONTACTS,
                stateService.getOperation(chatId)
        );

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
        repository.add(new Contact(chatId, "Олег", "52367890", 34, Gender.MALE, false));
        repository.add(new Contact(chatId, "Игорь", "868645435", 23, Gender.MALE, false));
        repository.add(new Contact(chatId, "Юлия", "52367890", 76, Gender.FEMALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Получить все");
        Assertions.assertEquals(
                Operation.GET_ALL_CONTACTS,
                stateService.getOperation(chatId)
        );

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
        repository.add(new Contact(chatId, "Олег", "52367890", 34, Gender.MALE, false));
        repository.add(new Contact(chatId, "Игорь", "868645435", 23, Gender.MALE, false));
        repository.add(new Contact(chatId, "Юлия", "52367890", 76, Gender.FEMALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Получить все");
        Assertions.assertEquals(
                Operation.GET_ALL_CONTACTS,
                stateService.getOperation(chatId)
        );

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
        repository.add(new Contact(chatId, "Олег", "52367890", 34, Gender.MALE, false));
        repository.add(new Contact(chatId, "Игорь", "868645435", 23, Gender.MALE, false));
        repository.add(new Contact(chatId, "Юлия", "52367890", 76, Gender.FEMALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Получить все");
        Assertions.assertEquals(
                Operation.GET_ALL_CONTACTS,
                stateService.getOperation(chatId)
        );

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
        repository.add(new Contact(chatId, "Олег", "52367890", 34, Gender.MALE, false));
        repository.add(new Contact(chatId, "Игорь", "868645435", 23, Gender.MALE, false));
        repository.add(new Contact(chatId, "Юлия", "52367890", 76, Gender.FEMALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Получить все");
        Assertions.assertEquals(
                Operation.GET_ALL_CONTACTS,
                stateService.getOperation(chatId)
        );

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
        repository.add(new Contact(chatId, "Олег", "1234567890", 42, Gender.MALE, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Найти");
        handler.handleMessage(chatId, "Поиск по имени");
        handler.handleMessage(chatId, "Олег");
        handler.handleInlineButtonActivated(chatId, "CURRENT_CONTACT_MENU_Олег");
        BotResponse response = handler.handleMessage(chatId, "Информация");

        Assertions.assertEquals("""
                Контакт: Олег
                Номер: 1234567890
                Пол: мужской
                Возраст: 42
                Не заблокирован""", response.getText());
        Assertions.assertEquals(
                Operation.CONTACTS_MENU,
                stateService.getOperation(chatId)
        );
    }

    /**
     * Тестируем информацию о пользователе, у которого задано только имя
     */
    @Test
    public void getContactInfoOnlyNameTest(){
        repository.add(new Contact(chatId, "Олег", "", -1, Gender.NOT_SPECIFIED, false));

        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Найти");
        handler.handleMessage(chatId, "Поиск по имени");
        handler.handleMessage(chatId, "Олег");
        handler.handleInlineButtonActivated(chatId, "CURRENT_CONTACT_MENU_Олег");
        BotResponse response = handler.handleMessage(chatId, "Информация");

        Assertions.assertEquals("""
                Контакт: Олег
                Номер: не указан
                Пол: не указан
                Возраст: не указан
                Не заблокирован""", response.getText());
    }
}