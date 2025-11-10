//package operations;
//
//import org.example.operations.FindContactHandler;
//import org.example.operations.OperationHandler;
//import org.example.service.ContactService;
//import org.example.state.Operation;
//import org.example.state.State;
//import org.junit.Assert;
//import org.junit.Test;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//
///**
// * Тесты для FindContactHandler
// */
//public class FindContactHandlerTest {
//    /**
//     * Обработчик сообщений
//     */
//    public final OperationHandler handler = new FindContactHandler();
//    /**
//     * Состояние диалога. Контекст
//     */
//    public final State state = new State();
//    /**
//     * Сервис контактов
//     */
//    public final ContactService service = new ContactService();
//    /**
//     * ChatId пользователя
//     */
//    public final Long chatId = 123245663L;
//
//    /**
//     * Проверяем корректность ответа после успешного поиска по имени
//     */
//    @Test
//    public void findContactByNameTest(){
//        HashMap<String, String> contactMap = new LinkedHashMap<>() {{
//            put("contactName", "Владелец");
//            put("contactAge", "20");
//            put("contactNumber", "124526575");
//        }};
//        service.tryAddContact(chatId, contactMap);
//        state.changeCurrentOperation(Operation.FIND_CONTACT, true);
//
//        handler.handleMessage(service, state, "Поиск по имени", chatId);
//        SendMessage response = handler.handleMessage(service, state, "Владелец", chatId);
//        service.deleteByName(chatId, "Владелец");
//
//        Assert.assertEquals("По имени Владелец контакт успешно найден.", response.getText());
//    }
//
//    /**
//     * Проверяем корректность ответа после успешного поиска по номеру
//     */
//    @Test
//    public void findContactByNumberTest(){
//        HashMap<String, String> contactMap = new LinkedHashMap<>() {{
//            put("contactName", "Кран");
//            put("contactAge", "20");
//            put("contactNumber", "12452654475");
//        }};
//        service.tryAddContact(chatId, contactMap);
//        state.changeCurrentOperation(Operation.FIND_CONTACT, true);
//
//        handler.handleMessage(service, state, "Поиск по номеру", chatId);
//        SendMessage response = handler.handleMessage(service, state, "12452654475", chatId);
//        service.deleteByName(chatId, "Кран");
//
//        Assert.assertEquals("По номеру 12452654475 контакт успешно найден.", response.getText());
//    }
//}
