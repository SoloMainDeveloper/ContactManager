package org.example.operations;

import org.example.KeyboardCreator;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public class ContactsMenuHandler implements OperationHandler {
    @Override
    public SendMessage handleMessage(ContactService service, State state, String messageText, Long chatId) {
        SendMessage response = new SendMessage();
        switch (messageText) {
            case "Добавить":
                state.changeCurrentOperation(Operation.ADD_CONTACT);
                response.setText("Напишите имя добавляемого контакта");
                state.setLastRequestedParamKey("contactName");
                break;
            case "Получить все":
                state.changeCurrentOperation(Operation.GET_ALL_CONTACTS);
                //TODO
                break;
            case "Найти":
                state.changeCurrentOperation(Operation.FIND_CONTACT);
                response.setText("Выберите по какому признаку будет произведен поиск");
                response.setReplyMarkup(new KeyboardCreator().createKeyboard(List.of("Поиск по имени", "Поиск по номеру")));
                break;
            case "Назад":
                state.changeCurrentOperation(Operation.MAIN_MENU);
                //TODO
                break;
            default:
                response.setText("Я не понимаю эту команду");
                break;
        }
        return response;
    }
}
