//package operations;
//
//import org.example.operations.EditContactHandler;
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
// * Тесты для EditContactHandler
// */
//public class EditContactHandlerTest {
//    /**
//     * Проверка успешного редактирования всех параметров контакта
//     */
//    @Test
//    public void editContactTest(){
//        OperationHandler handler = new EditContactHandler();
//        State state = new State();
//        ContactService service = new ContactService();
//        Long chatId = 123245663L;
//        HashMap<String, String> contactMap = new LinkedHashMap<>() {{
//            put("contactName", "Владелец");
//            put("contactAge", "20");
//            put("contactNumber", "124526575");
//            put("contactGender", "Мужской");
//        }};
//        service.tryAddContact(chatId, contactMap);
//        state.changeCurrentOperation(Operation.EDIT_CONTACT, true);
//        state.addParameter("currentContactName", "Владелец");
//
//        handler.handleMessage(service, state, "Имя", chatId);
//        handler.handleMessage(service, state, "Григорий", chatId);
//        handler.handleMessage(service, state, "Номер", chatId);
//        handler.handleMessage(service, state, "+99923333", chatId);
//        handler.handleMessage(service, state, "Возраст", chatId);
//        handler.handleMessage(service, state, "43", chatId);
//        handler.handleMessage(service, state, "Пол", chatId);
//        handler.handleMessage(service, state, "Мужской", chatId);
//
//        SendMessage response = handler.handleMessage(service, state, "Изменить контакт", chatId);
//        service.deleteByName(chatId, "Григорий");
//
//        Assert.assertEquals("Контакт Владелец успешно изменен", response.getText());
//    }
//}
