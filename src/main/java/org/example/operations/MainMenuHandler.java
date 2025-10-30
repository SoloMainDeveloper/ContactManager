package org.example.operations;

import org.example.keyboardcreator.ReplyKeyboardCreator;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class MainMenuHandler implements OperationHandler {
    @Override
    public SendMessage handleMessage(ContactService service, State state, String messageText, Long chatId) {
        SendMessage response = new SendMessage();
        switch (messageText) {
            case "/start":
                response.setText("Привет! Я бот для управления контактами.");
                response.setReplyMarkup(new ReplyKeyboardCreator().mainMenu());
                break;
            case "Контакты":
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                response.setText("Взаимодействие с контактами. Выберите какое действие хотите совершить");
                response.setReplyMarkup(new ReplyKeyboardCreator().contactsMenu());
                break;
            default:
                response.setText("Я не понимаю эту команду");
                break;
        }
        return response;
    }
}
