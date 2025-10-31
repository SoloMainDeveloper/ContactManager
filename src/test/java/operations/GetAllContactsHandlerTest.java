package operations;

import org.example.operations.DeleteContactHandler;
import org.example.operations.GetAllContactsHandler;
import org.example.operations.OperationHandler;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.junit.Assert;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Тесты для GetAllContactsHandler
 */
public class GetAllContactsHandlerTest {
    /**
     * Обработчик сообщений
     */
    public final OperationHandler handler = new GetAllContactsHandler();
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
    public final Long chatId = 567234682L;


    /**
     * Проверка получения всех контактов
     */
    @Test
    public void getAllContactsTest(){
        HashMap<String, String> contactMap = new LinkedHashMap<>() {{
            put("contactName", "Игрок 1");
            put("contactAge", "10");
            put("contactNumber", "111111111");
        }};
        service.tryAddContact(chatId, contactMap);
        HashMap<String, String> contactMap2 = new LinkedHashMap<>() {{
            put("contactName", "Игрок 2");
            put("contactAge", "20");
            put("contactNumber", "2222222222");
        }};
        service.tryAddContact(chatId, contactMap2);
        HashMap<String, String> contactMap3 = new LinkedHashMap<>() {{
            put("contactName", "Игрок 3");
            put("contactAge", "30");
            put("contactNumber", "333333333");
        }};
        service.tryAddContact(chatId, contactMap3);

        state.changeCurrentOperation(Operation.GET_ALL_CONTACTS, true);
        SendMessage response = handler.handleMessage(service, state, "Получить", chatId);

        Assert.assertEquals("Все контакты", response.getText());
    }
}
