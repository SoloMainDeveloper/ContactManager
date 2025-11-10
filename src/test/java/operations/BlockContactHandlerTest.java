//package operations;
//
//import org.example.operations.BlockContactHandler;
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
// * Тесты для BlockContactHandler
// */
//public class BlockContactHandlerTest {
//    /**
//     * Обработчик сообщений
//     */
//    public final OperationHandler handler = new BlockContactHandler();
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
//     * Создаём пользователя. Проверяем, корректность сообщения о его успешной блокировке
//     */
//    @Test
//    public void blockContactTest(){
//        HashMap<String, String> contactMap = new LinkedHashMap<>() {{
//            put("contactName", "Владелец");
//            put("contactAge", "20");
//            put("contactNumber", "124526575");
//        }};
//        service.tryAddContact(chatId, contactMap);
//
//        state.changeCurrentOperation(Operation.BLOCK_CONTACT, true);
//        state.addParameter("currentContactName", "Владелец");
//
//        SendMessage response = handler.handleMessage(service, state, "Да", chatId);
//        service.deleteByName(chatId, "Владелец");
//
//        Assert.assertEquals("Контакт Владелец успешно заблокирован", response.getText());
//    }
//
//    /**
//     * Проверка на блокировку несуществующего пользователя
//     */
//    @Test
//    public void blockNonExistentContactTest(){
//        state.changeCurrentOperation(Operation.BLOCK_CONTACT, true);
//        state.addParameter("currentContactName", "Владелец");
//        SendMessage response = handler.handleMessage(service, state, "Да", chatId);
//        Assert.assertEquals("Контакта с именем Владелец не существует. Блокировка не применена", response.getText());
//    }
//}
