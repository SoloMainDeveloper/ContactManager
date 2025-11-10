//package operations;
//
//import org.example.operations.DeleteContactHandler;
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
// * Тесты для DeleteContactHandler
// */
//public class DeleteContactHandlerTest {
//    /**
//     * Обработчик сообщений
//     */
//    public final OperationHandler handler = new DeleteContactHandler();
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
//     * Создаём пользователя. Проверяем корректность сообщения об успешном удалении данного пользователя
//     */
//    @Test
//    public void deleteContactTest(){
//        HashMap<String, String> contactMap = new LinkedHashMap<>() {{
//            put("contactName", "Владелец");
//            put("contactAge", "20");
//            put("contactNumber", "124526575");
//        }};
//        service.tryAddContact(chatId, contactMap);
//
//        state.changeCurrentOperation(Operation.DELETE_CONTACT, true);
//        state.addParameter("currentContactName", "Владелец");
//
//        SendMessage response = handler.handleMessage(service, state, "Да", chatId);
//
//        Assert.assertEquals("Контакт Владелец успешно удален", response.getText());
//    }
//}
