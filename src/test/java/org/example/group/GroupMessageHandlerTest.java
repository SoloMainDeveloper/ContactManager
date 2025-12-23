package org.example.group;

import org.example.MessageHandler;
import org.example.contact.FakeContactRepository;
import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.entity.Group;
import org.example.operations.MainMenuHandler;
import org.example.operations.group.*;
import org.example.response.BotResponse;
import org.example.service.ContactService;
import org.example.service.GroupService;
import org.example.service.StateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

/**
 * Тестируем обработчики сообщений, взаимодействующие с группами
 */
public class GroupMessageHandlerTest {
    /**
     * Фейковый групп-репозиторий для тестов
     */
    private FakeGroupRepository fakeGroupRepository;

    /**
     * Групп-сервис для тестов
     */
    private GroupService groupService;

    /**
     * Обработчик сообщений
     */
    private MessageHandler handler;

    /**
     * ChatId пользователя
     */
    private static final Long chatId = 123245663L;

    /**
     * Инициализируем фейковые репозитории, чтобы не работать напрямую с БД.
     * Инициализируем сервисы для более детального тестирования внутренностей.
     * Инициализируем MessageHandler. Добавляем несколько контактов в
     * fakeContactRepository
     */
    @BeforeEach
    public void setup() {
        fakeGroupRepository = new FakeGroupRepository();
        groupService = new GroupService(fakeGroupRepository);
        FakeContactRepository fakeContactRepository = new FakeContactRepository();
        ContactService contactService = new ContactService(fakeContactRepository);
        StateService stateService = new StateService();
        handler = new MessageHandler(
            List.of(new AddGroupHandler(groupService, contactService, stateService),
                new GroupsMenuHandler(stateService),
                new CurrentGroupMenuHandler(
                    groupService, contactService, stateService),
                new DeleteGroupHandler(groupService, stateService),
                new EditGroupHandler(groupService, contactService, stateService),
                new FindGroupHandler(groupService, stateService),
                new GetAllGroupsHandler(groupService, stateService),
                new MainMenuHandler(stateService)),
            stateService);

        fakeContactRepository.add(new Contact(
            chatId, "Юлия", "95436575", 34, Gender.FEMALE, false));
        fakeContactRepository.add(new Contact(
            chatId, "Олег", "12345", 45, Gender.MALE, false));
        fakeContactRepository.add(new Contact(
            chatId, "Михаил", "11", 21, Gender.MALE, true));
    }

    /**
     * Тестируем успешное добавление группы, добавление в неё существующего и не
     * существующего контактов и затем её поиск по имени
     */
    @Test
    public void addGroupTest() {
        handler.handleMessage(chatId, "Группы");
        handler.handleMessage(chatId, "Добавить");

        handler.handleMessage(chatId, "Друзья");
        handler.handleMessage(chatId, "Добавить контакт в группу");
        handler.handleMessage(chatId, "Юлия");
        handler.handleMessage(chatId, "Добавить контакт в группу");
        Assertions.assertEquals(
            "Контакт Константин не был найден.",
            handler.handleMessage(chatId, "Константин").getText()
        );
        Assertions.assertEquals(
            "Группа Друзья успешна сохранена",
            handler.handleMessage(chatId, "Сохранить группу").getText()
        );
        Group group = groupService.findGroupByName(chatId, "Друзья").orElseThrow();
        List<Contact> contacts = group.getContacts();
        Assertions.assertEquals(1, contacts.size());
        Contact contact = contacts.getFirst();
        Assertions.assertEquals("Юлия", contact.getName());
        Assertions.assertEquals("95436575", contact.getPhoneNumber());
        Assertions.assertEquals(34, contact.getAge());
        Assertions.assertEquals(Gender.FEMALE, contact.getGender());
        Assertions.assertFalse(contact.isBlocked());

        handler.handleMessage(chatId, "Найти");
        Assertions.assertEquals(
            "Группа Друзья успешно найдена",
            handler.handleMessage(chatId, "Друзья").getText()
        );
        handler.handleInlineButtonActivated(chatId, "CURRENT_GROUP_MENU_Друзья");
        BotResponse response = handler.handleMessage(
            chatId, "Вывести все контакты группы");
        Assertions.assertEquals(
            List.of("Юлия"),
            response.getInlineKeyboardText().inlineText());
    }

    /**
     * Неуспешное добавление группы, так как уже существует группа с таким именем
     */
    @Test
    public void addGroupThatAlreadyExistsTest() {
        handler.handleMessage(chatId, "Группы");
        handler.handleMessage(chatId, "Добавить");
        handler.handleMessage(chatId, "Друзья");
        handler.handleMessage(chatId, "Сохранить группу");

        handler.handleMessage(chatId, "Добавить");
        handler.handleMessage(chatId, "Друзья");
        handler.handleMessage(chatId, "Добавить контакт в группу");
        handler.handleMessage(chatId, "Юлия");
        BotResponse response = handler.handleMessage(chatId, "Сохранить группу");
        Assertions.assertEquals(
            "Произошла ошибка при добавлении группы: Группа Друзья уже существует",
            response.getText());

        Group group = groupService.findGroupByName(chatId, "Друзья").orElseThrow();
        Assertions.assertTrue(group.getContacts().isEmpty());
    }

    /**
     * Тестируем успешное удаление группы
     */
    @Test
    public void deleteGroupTest() {
        fakeGroupRepository.add(new Group(chatId, "Друзья"));

        handler.handleMessage(chatId, "Группы");
        handler.handleMessage(chatId, "Найти");
        handler.handleMessage(chatId, "Друзья");
        handler.handleInlineButtonActivated(chatId, "CURRENT_GROUP_MENU_Друзья");
        handler.handleMessage(chatId, "Удалить");

        Assertions.assertEquals(
            "Группа Друзья успешно удалена",
            handler.handleMessage(chatId, "Да").getText()
        );
        Assertions.assertTrue(groupService.findGroupByName(chatId, "Друзья").isEmpty());
        BotResponse response = handler.handleInlineButtonActivated(
            chatId, "CURRENT_GROUP_MENU_Друзья");
        Assertions.assertEquals("Группа Друзья не была найдена", response.getText());
    }

    /**
     * Тестируем успешное редактирование группы
     * <ol>
     *     <li>Изменение имени</li>
     *     <li>Удаление контакта из группы</li>
     *     <li>Добавление контакта в группу</li>
     * </ol>
     */
    @Test
    public void editContactTest() {
        handler.handleMessage(chatId, "Группы");
        handler.handleMessage(chatId, "Добавить");
        handler.handleMessage(chatId, "Магазин");
        handler.handleMessage(chatId, "Добавить контакт в группу");
        handler.handleMessage(chatId, "Юлия");
        handler.handleMessage(chatId, "Сохранить группу");
        handler.handleInlineButtonActivated(chatId, "CURRENT_GROUP_MENU_Магазин");

        handler.handleMessage(chatId, "Изменить");
        handler.handleMessage(chatId, "Изменить имя группы");
        handler.handleMessage(chatId, "Товарищи");
        handler.handleMessage(chatId, "Добавить контакт в группу");
        handler.handleMessage(chatId, "Олег");
        handler.handleMessage(chatId, "Удалить контакт из группы");
        handler.handleMessage(chatId, "Юлия");
        BotResponse response = handler.handleMessage(chatId, "Сохранить группу");
        Assertions.assertEquals(
            "Группа успешно отредактирована и сохранена",
            response.getText()
        );

        Optional<Group> oldGroup = groupService.findGroupByName(chatId, "Друзья");
        Assertions.assertTrue(oldGroup.isEmpty());

        Group updatedGroup = groupService
            .findGroupByName(chatId, "Товарищи").orElseThrow();
        Assertions.assertEquals("Товарищи", updatedGroup.getName());

        List<Contact> contacts = updatedGroup.getContacts();
        Assertions.assertEquals(1, contacts.size());
        Contact contact = contacts.getFirst();
        Assertions.assertEquals("Олег", contact.getName());
        Assertions.assertEquals("12345", contact.getPhoneNumber());
        Assertions.assertEquals(45, contact.getAge());
        Assertions.assertEquals(Gender.MALE, contact.getGender());
        Assertions.assertFalse(contact.isBlocked());
    }

    /**
     * Тестируем поиск несуществующей группы по имени
     */
    @Test
    public void findNonExistingGroupByNameTest() {
        fakeGroupRepository.add(new Group(chatId, "Футболисты"));

        handler.handleMessage(chatId, "Группы");
        handler.handleMessage(chatId, "Найти");
        BotResponse response = handler.handleMessage(chatId, "Друзья");
        Assertions.assertEquals("По имени Друзья группа не найдена", response.getText());
    }

    /**
     * Тестируем получение всех групп
     * <p>Настройка: создаём три группы с разным количеством участников</p>
     * <p>Проверки</p>
     * <ol>
     *     <li>Вывод всех групп</li>
     *     <li>Вывод с сортировкой по алфавиту</li>
     *     <li>Вывод с сортировкой в обратном алфавитном порядке</li>
     *     <li>Вывод с сортировкой по убыванию кол-ва участников группы</li>
     *     <li>Вывод с сортировкой по возрастанию кол-ва участников группы</li>
     * </ol>
     */
    @Test
    public void getAllGroupsTest() {
        handler.handleMessage(chatId, "Группы");
        handler.handleMessage(chatId, "Добавить");
        handler.handleMessage(chatId, "Друзья");
        handler.handleMessage(chatId, "Добавить контакт в группу");
        handler.handleMessage(chatId, "Юлия");
        handler.handleMessage(chatId, "Добавить контакт в группу");
        handler.handleMessage(chatId, "Олег");
        handler.handleMessage(chatId, "Добавить контакт в группу");
        handler.handleMessage(chatId, "Михаил");
        handler.handleMessage(chatId, "Сохранить группу");

        handler.handleMessage(chatId, "Группы");
        handler.handleMessage(chatId, "Добавить");
        handler.handleMessage(chatId, "Коллеги");
        handler.handleMessage(chatId, "Добавить контакт в группу");
        handler.handleMessage(chatId, "Юлия");
        handler.handleMessage(chatId, "Добавить контакт в группу");
        handler.handleMessage(chatId, "Михаил");
        handler.handleMessage(chatId, "Сохранить группу");

        handler.handleMessage(chatId, "Группы");
        handler.handleMessage(chatId, "Добавить");
        handler.handleMessage(chatId, "Баскетбол");
        handler.handleMessage(chatId, "Добавить контакт в группу");
        handler.handleMessage(chatId, "Михаил");
        handler.handleMessage(chatId, "Сохранить группу");

        handler.handleMessage(chatId, "Группы");
        handler.handleMessage(chatId, "Получить все");
        BotResponse response = handler.handleMessage(chatId, "Получить");
        Assertions.assertEquals("Все ваши группы:", response.getText());
        Assertions.assertEquals(
            List.of("Друзья", "Коллеги", "Баскетбол"),
            response.getInlineKeyboardText().inlineText()
        );

        handler.handleMessage(chatId, "Сортировать");
        BotResponse responseSort1 = handler.handleMessage(
            chatId, "В алфавитном порядке имени");
        Assertions.assertEquals(
            "Все группы с выбранной сортировкой:",
            responseSort1.getText()
        );
        Assertions.assertEquals(
            List.of("Баскетбол", "Друзья", "Коллеги"),
            responseSort1.getInlineKeyboardText().inlineText()
        );

        handler.handleMessage(chatId, "Сортировать");
        Assertions.assertEquals(
            List.of("Коллеги", "Друзья", "Баскетбол"),
            handler.handleMessage(chatId, "В обратном алфавитному порядку имени")
                .getInlineKeyboardText().inlineText()
        );

        handler.handleMessage(chatId, "Сортировать");
        Assertions.assertEquals(
            List.of("Друзья", "Коллеги", "Баскетбол"),
            handler.handleMessage(chatId, "В порядке убывания кол-ва участников")
                .getInlineKeyboardText().inlineText()
        );

        handler.handleMessage(chatId, "Сортировать");
        Assertions.assertEquals(
            List.of("Баскетбол", "Коллеги", "Друзья"),
            handler.handleMessage(chatId, "В порядке возрастания кол-ва участников")
                .getInlineKeyboardText().inlineText()
        );
    }
}
