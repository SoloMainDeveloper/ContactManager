package operations;

import org.example.operations.AddContactHandler;
import org.example.operations.BlockContactHandler;
import org.example.operations.OperationHandler;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.junit.Assert;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Тесты для AddContactHandler
 */
public class AddContactHandlerTest {
    /**
     * Обработчик сообщений
     */
    public final OperationHandler handler = new AddContactHandler();
    /**
     * Состояние диалога. Контекст
     */
    public final State state = new State();
    /**
     * Сервис контактов
     */
    public final ContactService service = new ContactService();
    /**
     * ChatId пользователя
     */
    public final Long chatId = 123245663L;

    /**
     * Тест позитивного сценария добавления пользователя, у которого заданы все параметры
     */
    @Test
    public void addContactTest(){
        state.changeCurrentOperation(Operation.ADD_CONTACT, true);
        state.setLastRequestedParamKey("contactName");

        handler.handleMessage(service, state, "Олег", chatId);
        handler.handleMessage(service, state, "Номер", chatId);
        handler.handleMessage(service, state, "1234567890", chatId);
        handler.handleMessage(service, state, "Возраст", chatId);
        handler.handleMessage(service, state, "42", chatId);
        handler.handleMessage(service, state, "Пол", chatId);
        handler.handleMessage(service, state, "Мужской", chatId);
        SendMessage response = handler.handleMessage(service, state, "Сохранить контакт", chatId);
        service.deleteByName(chatId, "Олег");

        Assert.assertEquals("Контакт Олег успешно добавлен", response.getText());
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

        handler.handleMessage(service, state, "Олег", chatId);
        handler.handleMessage(service, state, "Номер", chatId);
        handler.handleMessage(service, state, "1234567890", chatId);
        handler.handleMessage(service, state, "Возраст", chatId);
        handler.handleMessage(service, state, "42", chatId);
        handler.handleMessage(service, state, "Пол", chatId);
        handler.handleMessage(service, state, "Мужской", chatId);
        SendMessage response = handler.handleMessage(service, state, "Сохранить контакт", chatId);
        service.deleteByName(chatId, "Олег");

        Assert.assertEquals("Контакт Олег не был добавлен. Произошла ошибка", response.getText());
    }
}
