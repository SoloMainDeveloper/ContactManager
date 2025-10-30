package org.example.operations;

import org.example.keyboardcreator.ReplyKeyboardCreator;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

public class ContactsMenuHandler implements OperationHandler {
    @Override
    public SendMessage handleMessage(ContactService service, State state, String messageText, Long chatId) {
        ReplyKeyboardCreator keyboardCreator = new ReplyKeyboardCreator();
        SendMessage response = new SendMessage();
        switch (messageText) {
            case "Добавить":
                state.changeCurrentOperation(Operation.ADD_CONTACT, true);
                response.setText("Напишите имя добавляемого контакта");
                state.setLastRequestedParamKey("contactName");
                break;
            case "Получить все":
                state.changeCurrentOperation(Operation.GET_ALL_CONTACTS, true);
                response.setText("Желаете получить все контакты сразу или добавить фильтрацию/сортировку?");
                response.setReplyMarkup(keyboardCreator.getAllContactsMenu());
                break;
            case "Найти":
                state.changeCurrentOperation(Operation.FIND_CONTACT, true);
                response.setText("Выберите по какому признаку будет произведен поиск");
                response.setReplyMarkup(keyboardCreator.createKeyboard(List.of("Поиск по имени", "Поиск по номеру")));
                break;
            case "Назад":
                response.setText("Вы вернулись назад");
                state.changeCurrentOperation(Operation.MAIN_MENU, true);
                response.setReplyMarkup(keyboardCreator.mainMenu());
                break;
            default:
                response.setText("Я не понимаю эту команду");
                break;
        }
        return response;
    }
}
