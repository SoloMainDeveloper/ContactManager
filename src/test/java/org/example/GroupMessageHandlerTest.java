package org.example;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.entity.Group;
import org.example.operations.MainMenuHandler;
import org.example.operations.group.*;
import org.example.response.BotResponse;
import org.example.service.ContactService;
import org.example.service.GroupService;
import org.example.service.StateService;
import org.example.utils.GroupConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * Тестируем обработчики сообщений, взаимодействующие с группами
 */
public class GroupMessageHandlerTest {
    /**
     * Преобразует поля id-шников контактов групп
     */
    private final GroupConverter converter = new GroupConverter();

    /**
     * Фейковый групп-репозиторий для тестов
     */
    private FakeGroupRepository fakeGroupRepository;

    /**
     * Фейковый контакт-репозиторий для тестов
     */
    private FakeContactRepository fakeContactRepository;

    /**
     * Групп-сервис для тестов
     */
    private GroupService groupService;

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
    public final Long chatId = 123245663L;

    /**
     * Инициализируем фейковые репозитории, чтобы не работать напрямую с БД.
     * Инициализируем сервисы для более детального тестирования внутренностей.
     * Инициализируем MessageHandler.
     */
    @BeforeEach
    public void setup() {
        fakeGroupRepository = new FakeGroupRepository();
        groupService = new GroupService(fakeGroupRepository);
        fakeContactRepository = new FakeContactRepository();
        contactService = new ContactService(fakeContactRepository);
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
    }

    /**
     * Тестируем успешное добавление группы
     */
    @Test
    public void addGroupTest() {
        fakeContactRepository.add(new Contact(
                chatId, "Юлия", "95436575", 34, Gender.FEMALE, false));

        handler.handleMessage(chatId, "Группы");
        handler.handleMessage(chatId, "Добавить");

        handler.handleMessage(chatId, "Друзья");
        handler.handleMessage(chatId, "Добавить контакт");
        handler.handleMessage(chatId, "Юлия");
        handler.handleMessage(chatId, "Добавить контакт");
        handler.handleMessage(chatId, "Константин");
        BotResponse response = handler.handleMessage(chatId, "Сохранить группу");
        Assertions.assertEquals(
                "Группа Друзья успешна сохранена",
                response.getText()
        );

        Group group = groupService
                .findGroupByName(chatId, "Друзья")
                .orElseThrow();
        Assertions.assertEquals("0", converter.contactIdsToString(group.getContactIds()));
    }

    /**
     * Неуспешное добавление группы, так как уже существует группа с таким именем
     */
    @Test
    public void addContactThatAlreadyExistsTest() {
        fakeContactRepository.add(new Contact(
                chatId, "Юлия", "95436575", 34, Gender.FEMALE, false));

        handler.handleMessage(chatId, "Группы");
        handler.handleMessage(chatId, "Добавить");
        handler.handleMessage(chatId, "Друзья");
        handler.handleMessage(chatId, "Сохранить группу");

        handler.handleMessage(chatId, "Добавить");
        handler.handleMessage(chatId, "Друзья");
        handler.handleMessage(chatId, "Добавить контакт");
        handler.handleMessage(chatId, "Юлия");
        BotResponse response = handler.handleMessage(
                chatId, "Сохранить группу");
        Assertions.assertEquals(
                "Произошла ошибка при добавлении группы: Группа Друзья уже существует",
                response.getText());

        Group group = groupService
                .findGroupByName(chatId, "Друзья")
                .orElseThrow();
        Assertions.assertEquals("", converter.contactIdsToString(group.getContactIds()));
    }

    /**
     * Тестируем успешное удаление группы
     */
    @Test
    public void deleteGroupTest() {
        fakeGroupRepository.add(new Group(chatId, "Друзья", new HashSet<>()));

        handler.handleMessage(chatId, "Группы");
        handler.handleMessage(chatId, "Найти");
        handler.handleMessage(chatId, "Друзья");
        handler.handleInlineButtonActivated(chatId, "CURRENT_GROUP_MENU_Друзья");
        handler.handleMessage(chatId, "Удалить");
        BotResponse response = handler.handleMessage(chatId, "Да");

        Assertions.assertEquals("Группа Друзья успешно удалена", response.getText());
        Assertions.assertTrue(groupService.findGroupByName(chatId, "Друзья").isEmpty());
    }

    /**
     * Тестируем успешное редактирование группы
     */
    @Test
    public void editContactTest() {
        fakeGroupRepository.add(new Group(chatId, "Друзья", new HashSet<>()));
        fakeContactRepository.add(new Contact(
                chatId, "Олег", "95436475", 34, Gender.MALE, false));

        handler.handleMessage(chatId, "Группы");
        handler.handleMessage(chatId, "Найти");
        handler.handleMessage(chatId, "Друзья");

        handler.handleInlineButtonActivated(chatId, "CURRENT_GROUP_MENU_Друзья");
        handler.handleMessage(chatId, "Изменить");
        handler.handleMessage(chatId,"Изменить имя группы");
        handler.handleMessage(chatId,"Товарищи");
        handler.handleMessage(chatId,"Добавить контакт в группу");
        handler.handleMessage(chatId,"Олег");
        BotResponse response = handler.handleMessage(chatId,"Сохранить группу");
        Assertions.assertEquals(
                "Группа успешно отредактирована и сохранена",
                response.getText()
        );

        Optional<Group> oldGroup = groupService.findGroupByName(chatId, "Друзья");
        Assertions.assertTrue(oldGroup.isEmpty());

        Group updatedGroup = groupService
                .findGroupByName(chatId, "Товарищи").orElse(null);
        Assertions.assertNotNull(updatedGroup);
        Assertions.assertEquals("Товарищи", updatedGroup.getName());
        Assertions.assertEquals("0", converter
                .contactIdsToString(updatedGroup.getContactIds()));
        Assertions.assertNotNull(contactService.findContactById(chatId, 0L));
    }

    /**
     * Тестируем успешный поиск группы по имени
     */
    @Test
    public void findContactByNameTest() {
        fakeGroupRepository.add(new Group(chatId, "Друзья", new HashSet<>()));

        handler.handleMessage(chatId, "Группы");
        handler.handleMessage(chatId, "Найти");
        BotResponse response =handler.handleMessage(chatId, "Друзья");
        Assertions.assertEquals(
                "Группа Друзья успешно найдена",
                response.getText()
        );
        Optional<Group> group = groupService.findGroupByName(chatId, "Друзья");
        Assertions.assertTrue(group.isPresent());
    }

    /**
     * Тестируем получение всех групп
     */
    @Test
    public void getAllGroupsTest() {
        fakeGroupRepository.add(new Group(chatId, "Друзья", new HashSet<>()));
        fakeGroupRepository.add(new Group(chatId, "Коллеги", new HashSet<>()));
        fakeGroupRepository.add(new Group(chatId, "Баскетбол", new HashSet<>()));

        handler.handleMessage(chatId, "Группы");
        handler.handleMessage(chatId, "Получить все");
        BotResponse response = handler.handleMessage(chatId, "Получить");
        Assertions.assertEquals("Все ваши группы:", response.getText());
        Assertions.assertEquals(
                List.of("Друзья", "Коллеги", "Баскетбол"),
                response.getInlineKeyboardText().inlineText()
        );
    }

    /**
     * Тестируем получение групп с сортировкой в алфавитном порядке по имени
     */
    @Test
    public void getAllGroupsWithSorterByNameTest() {
        fakeGroupRepository.add(new Group(chatId, "Друзья", new HashSet<>()));
        fakeGroupRepository.add(new Group(chatId, "Коллеги", new HashSet<>()));
        fakeGroupRepository.add(new Group(chatId, "Баскетбол", new HashSet<>()));

        handler.handleMessage(chatId, "Группы");
        handler.handleMessage(chatId, "Получить все");
        handler.handleMessage(chatId, "Сортировать");
        BotResponse response = handler
                .handleMessage(chatId, "В алфавитном порядке имени");
        Assertions.assertEquals(
                "Все группы с выбранной сортировкой:",
                response.getText()
        );
        Assertions.assertEquals(
                List.of("Баскетбол", "Друзья", "Коллеги"),
                response.getInlineKeyboardText().inlineText()
        );
    }
}
