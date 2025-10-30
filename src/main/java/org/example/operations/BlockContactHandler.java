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
                String contactName = state.getParamByKey("currentContactName");
                Contact contact = service.findContactByName(chatId, contactName);

                contact.setBlocked(!contact.isBlocked());
                service.update(contact);

                String blockActionInfo = contact.isBlocked() ? "заблокирован" : "разблокирован";
                String responseText = String.format("Контакт " + contactName + " успешно %s", blockActionInfo);

                response.setText(responseText);
                response.setReplyMarkup(keyboardCreator.contactsMenu());
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                break;
            case "Нет":
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                response.setText("Действие изменения блокировки отменено");
                response.setReplyMarkup(keyboardCreator.contactsMenu());
                break;
            default:
                response.setText("Я не понимаю эту команду.");
                break;
        }
        return response;
    }
}
