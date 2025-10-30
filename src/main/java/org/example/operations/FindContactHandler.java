package org.example.operations;

import org.example.keyboardcreator.InlineKeyboardCreator;
import org.example.keyboardcreator.ReplyKeyboardCreator;
import org.example.entity.Contact;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

public class FindContactHandler implements OperationHandler {
    @Override
    public SendMessage handleMessage(ContactService service, State state, String messageText, Long chatId) {
        SendMessage response = new SendMessage();
        switch(messageText){
            case "Поиск по имени":
                response.setText("Введите имя");
                state.setLastRequestedParamKey("contactName");
                break;
            case "Поиск по номеру":
                response.setText("Введите номер");
                state.setLastRequestedParamKey("contactNumber");
                break;
            default:
                return handleMessageWithContext(service, state, messageText, chatId);
        }
        return response;
    }

    private SendMessage handleMessageWithContext(ContactService service, State state, String messageText, Long chatId) {
        SendMessage response = new SendMessage();
        String lastRequestedParamKey = state.getLastRequestedParamKey();
        if (lastRequestedParamKey == null) {
            response.setText("Я не понимаю эту команду.");
            return response;
        }
        state.addParameter(lastRequestedParamKey, messageText);

        switch(lastRequestedParamKey){
            case "contactName": {
                String name = state.getParamByKey("contactName");
                Contact contact = service.findContactByName(chatId, name);
                if(contact != null) {
                    response.setText("По имени " + name + " контакт успешно найден.");
                    response.setReplyMarkup(new InlineKeyboardCreator().createKeyboard(List.of(contact.getName()),
                            Operation.CURRENT_CONTACT_MENU.toString()));
                } else {
                    response.setText("По имени " + name + " контакты не найдены.");
                    response.setReplyMarkup(new ReplyKeyboardCreator().contactsMenu());
                }
                break;
            }
            case "contactNumber": {
                String number = state.getParamByKey("contactNumber");
                Contact contact = service.findContactByNumber(chatId, number);
                if(contact != null) {
                    response.setText("По номеру " + number + " контакт успешно найден.");
                    response.setReplyMarkup(new InlineKeyboardCreator().createKeyboard(List.of(contact.getName()),
                            Operation.CURRENT_CONTACT_MENU.toString()));
                } else {
                    response.setText("По номеру " + number + " контакты не найдены.");
                    response.setReplyMarkup(new ReplyKeyboardCreator().contactsMenu());
                }
                break;
            }
        }

        return response;
    }
}
