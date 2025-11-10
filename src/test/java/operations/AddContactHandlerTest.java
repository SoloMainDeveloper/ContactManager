package operations;

import org.example.entity.Contact;
import org.example.operations.AddContactHandler;
import org.example.response.BotResponse;
import org.example.state.Operation;
import org.example.state.State;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Тесты для AddContactHandler
 */
public class AddContactHandlerTest {
    private FakeRepository repository;
    private FakeContactService service;

    /**
     * Обработчик сообщений
     */
    private AddContactHandler handler;

    /**
     * Состояние диалога. Контекст
     */
    public State state;

    /**
     * ChatId пользователя
     */
    public final Long chatId = 123245663L;

    /**
     * Заново инициализируем state перед каждым тестом
     */
    @BeforeEach
    public void setupState(){
        repository = new FakeRepository();
        service = new FakeContactService(repository);
        handler = new AddContactHandler(service);
        state = new State();
    }

    /**
     * Тест позитивного сценария добавления пользователя, у которого заданы все параметры
     */
    @Test
    public void addContactTest(){
        state.changeCurrentOperation(Operation.ADD_CONTACT, true);
        state.setLastRequestedParamKey("contactName");

        handler.handleMessage(state, "Олег", chatId);
        handler.handleMessage(state, "Номер", chatId);
        handler.handleMessage(state, "1234567890", chatId);
        handler.handleMessage(state, "Возраст", chatId);
        handler.handleMessage(state, "42", chatId);
        handler.handleMessage(state, "Пол", chatId);
        handler.handleMessage(state, "Мужской", chatId);
        BotResponse response = handler.handleMessage(
                state, "Сохранить контакт", chatId);

        Assertions.assertEquals("Контакт Олег успешно добавлен", response.getText());
        Assertions.assertEquals(1, repository.getCurrentContactsSize(chatId));
        Contact contact = service.findContactByName(chatId, "Олег").orElse(null);
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
    public void addContactThatAlreadyExistsTest(){
        HashMap<String, String> contactMap = new LinkedHashMap<>() {{
            put("contactName", "Олег");
            put("contactAge", "20");
        }};
        service.tryAddContact(chatId, contactMap);
        state.setLastRequestedParamKey("contactName");
        state.changeCurrentOperation(Operation.ADD_CONTACT, true);

        handler.handleMessage(state, "Олег", chatId);
        handler.handleMessage(state, "Номер", chatId);
        handler.handleMessage(state, "1234567890", chatId);
        handler.handleMessage(state, "Возраст", chatId);
        handler.handleMessage(state, "42", chatId);
        handler.handleMessage(state, "Пол", chatId);
        handler.handleMessage(state, "Мужской", chatId);
        BotResponse response = handler.handleMessage(
                state, "Сохранить контакт", chatId);
        Assertions.assertEquals(1, repository.getCurrentContactsSize(chatId));
        Assertions.assertEquals(
                "Контакт Олег не был добавлен. Произошла ошибка",
                response.getText());
    }
}
