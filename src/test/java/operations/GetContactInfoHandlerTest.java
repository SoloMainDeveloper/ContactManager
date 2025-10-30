package operations;

import org.example.operations.GetContactInfoHandler;
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
 * Тесты для GetContactInfoHandler
 */
public class GetContactInfoHandlerTest {
    /**
     * Обработчик сообщений
     */
    public final OperationHandler handler = new GetContactInfoHandler();
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
     * Проверяем информацию о пользователе, у которого заданы все параметры
     */
    @Test
    public void getContactFullInfoTest(){
        HashMap<String, String> contactMap = new LinkedHashMap<>() {{
            put("contactName", "Владелец");
            put("contactAge", "20");
            put("contactNumber", "124526575");
            put("contactGender", "Мужской");
        }};
        service.tryAddContact(chatId, contactMap);
        state.changeCurrentOperation(Operation.GET_CONTACT_INFO, true);

        SendMessage response = handler.handleMessage(service, state, "Владелец", chatId);
        service.deleteByName(chatId, "Владелец");

        Assert.assertEquals("Контакт: Владелец\nНомер: 124526575\nПол: мужской\n" +
                "Возраст: 20\nНе заблокирован", response.getText());
    }

    /**
     * Проверяем информацию о пользователе, у которого задано только имя
     */
    @Test
    public void getContactInfoOnlyNameTest(){
        HashMap<String, String> contactMap = new LinkedHashMap<>() {{
            put("contactName", "Безымянный");
        }};
        service.tryAddContact(chatId, contactMap);
        state.changeCurrentOperation(Operation.GET_CONTACT_INFO, true);

        SendMessage response = handler.handleMessage(service, state, "Безымянный", chatId);
        service.deleteByName(chatId, "Безымянный");

        Assert.assertEquals("Контакт: Безымянный\nНомер: не указан\nПол: не указан\n" +
                "Возраст: не указан\nНе заблокирован", response.getText());
    }
}
