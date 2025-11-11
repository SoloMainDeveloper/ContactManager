package operations;

import org.example.MessageHandler;
import org.example.entity.Contact;
import org.example.operations.AddContactHandler;
import org.example.operations.ContactsMenuHandler;
import org.example.operations.MainMenuHandler;
import org.example.repository.StateRepository;
import org.example.response.BotResponse;
import org.example.service.StateService;
import org.example.state.Operation;
import org.junit.jupiter.api.*;

import java.util.List;

/**
 * Тесты для AddContactHandler
 */
public class AddContactHandlerTest {
    /**
     * Фейковый контакт-репозиторий для тестов
     */
    private FakeContactRepository repository;

    /**
     * Фейковый контакт-сервис для тестов
     */
    private FakeContactService contactService;

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
     * Создаём фейковый репозиторий - для того, чтобы без БД.
     * Создаём фейковый сервис - для более детального тестирования внутренностей.
     * Инициализируем MessageHandler (верхнеуровневая логика) только теми handler`ами,
     * которые будут использоваться.
     *
     */
    @BeforeEach
    public void setupState(){
        repository = new FakeContactRepository();
        contactService = new FakeContactService(repository);
        stateService = new StateService(new StateRepository());
        handler = new MessageHandler(
                List.of(new MainMenuHandler(stateService),
                        new ContactsMenuHandler(stateService),
                        new AddContactHandler(contactService, stateService)),
                stateService);
    }

    /**
     * Тест позитивного сценария добавления пользователя, у которого заданы все параметры
     */
    @Test
    public void addContactTest(){
        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Добавить");
        Assertions.assertEquals(Operation.ADD_CONTACT,
                stateService.getOperation(chatId));
        handler.handleMessage(chatId, "Олег");
        handler.handleMessage(chatId, "Номер");
        handler.handleMessage(chatId, "1234567890");
        handler.handleMessage(chatId, "Возраст");
        handler.handleMessage(chatId, "42");
        handler.handleMessage(chatId, "Пол");
        handler.handleMessage(chatId, "Мужской");
        BotResponse response = handler.handleMessage(
                chatId, "Сохранить контакт");

        Assertions.assertEquals("Контакт Олег успешно добавлен", response.getText());
        Assertions.assertEquals(1, repository.getCurrentContactsSize(chatId));
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
    }

    /**
     * Неуспешное добавление контакта, так как уже существует контакт с таким именем
     */
    @Test
    public void addContactThatAlreadyExistsTest(){
        handler.handleMessage(chatId, "Контакты");
        handler.handleMessage(chatId, "Добавить");
        handler.handleMessage(chatId, "Олег");
        handler.handleMessage(chatId, "Возраст");
        handler.handleMessage(chatId, "20");
        handler.handleMessage(chatId, "Сохранить контакт");

        Assertions.assertEquals(Operation.CONTACTS_MENU,
                stateService.getOperation(chatId));
        handler.handleMessage(chatId, "Добавить");
        Assertions.assertEquals(Operation.ADD_CONTACT,
                stateService.getOperation(chatId));
        handler.handleMessage(chatId, "Олег");
        handler.handleMessage(chatId, "Возраст");
        handler.handleMessage(chatId, "22");
        BotResponse response = handler.handleMessage(
                chatId, "Сохранить контакт");
        Assertions.assertEquals(1, repository.getCurrentContactsSize(chatId));
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
    }
}
