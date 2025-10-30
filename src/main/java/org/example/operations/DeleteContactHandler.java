package org.example.operations;

import org.example.keyboardcreator.ReplyKeyboardCreator;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Обработчик события: Удаление контакта
 */
public class DeleteContactHandler implements OperationHandler {
    @Override
    public SendMessage handleMessage(ContactService service, State state, String messageText, Long chatId) {
        ReplyKeyboardCreator keyboardCreator = new ReplyKeyboardCreator();
        SendMessage response = new SendMessage();
        switch(messageText) {
            case "Да":
                String contactName = state.getParamByKey("currentContactName");
                service.deleteByName(chatId, contactName);
                response.setText("Контакт " + contactName + " успешно удален");
                response.setReplyMarkup(keyboardCreator.contactsMenu());
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                break;
            case "Нет":
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                response.setText("Действие удаления текущего контакта отменено");
                response.setReplyMarkup(keyboardCreator.contactsMenu());
                break;
            default:
                response.setText("Я не понимаю эту команду.");
                break;
        }
        return response;
    }
}
