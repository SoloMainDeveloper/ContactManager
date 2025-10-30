package org.example.operations;

import org.example.entity.Contact;
import org.example.keyboardcreator.ReplyKeyboardCreator;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class BlockContactHandler implements OperationHandler {
    @Override
    public SendMessage handleMessage(ContactService service, State state, String messageText, Long chatId) {
        ReplyKeyboardCreator keyboardCreator = new ReplyKeyboardCreator();
        SendMessage response = new SendMessage();
        switch(messageText) {
            case "Да":
                state.changeCurrentOperation(Operation.CURRENT_CONTACT_MENU);

                String lastParamKey = state.getLastRequestedParamKey();
                String contactName = lastParamKey.substring("currentContactMenu_".length());
                Contact contact = service.findContactByName(chatId, contactName);

                contact.setBlocked(!contact.getBlocked());
                service.updateBlockField(contact);

                String blockActionInfo = contact.getBlocked() == false ? "заблокирован" : "разблокирован";
                String responseText = String.format("Текущий контакт успешно %s", blockActionInfo);

                response.setText(responseText);
                response.setReplyMarkup(keyboardCreator.currentContactMenu());
                break;
            case "Нет":
                state.changeCurrentOperation(Operation.CURRENT_CONTACT_MENU);
                response.setText("Действие изменения блокировки отменено");
                response.setReplyMarkup(keyboardCreator.currentContactMenu());
                break;
            default:
                response.setText("Я не понимаю эту команду.");
                break;
        }
        return response;
    }
}
