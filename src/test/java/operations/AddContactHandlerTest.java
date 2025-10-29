package operations;

import org.example.operations.AddContactHandler;
import org.example.operations.OperationHandler;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.junit.Assert;
import org.junit.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class AddContactHandlerTest {
    @Test
    public void addContactTest(){
        OperationHandler handler = new AddContactHandler();
        State state = new State();
        ContactService service = new ContactService();
        Long chatId = 123245663L;
        state.changeCurrentOperation(Operation.ADD_CONTACT);
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
}
