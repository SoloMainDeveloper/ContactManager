package org.example.operations;

import org.example.entity.Contact;
import org.example.keyboardcreator.ReplyKeyboardCreator;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

public class EditContactHandler implements OperationHandler{
    @Override
    public SendMessage handleMessage(ContactService service, State state, String messageText, Long chatId) {
        ReplyKeyboardCreator keyboardCreator = new ReplyKeyboardCreator();
        SendMessage response = new SendMessage();
        String contactName = state.getParamByKey("editContact");
        switch(messageText) {
            case "Имя":
                response.setText("Введите имя контакта");
                state.setLastRequestedParamKey("contactName");
            case "Номер":
                response.setText("Введите номер телефона");
                state.setLastRequestedParamKey("contactNumber");
                break;
            case "Возраст":
                response.setText("Введите возраст");
                state.setLastRequestedParamKey("contactAge");
                break;
            case "Пол":
                response.setText("Выберите пол");
                response.setReplyMarkup(keyboardCreator.createKeyboard(List.of("Мужской", "Женский")));
                state.setLastRequestedParamKey("contactGender");
                break;
            case "Изменить контакт":
                Contact oldContact = service.findContactByName(chatId, contactName);
                Boolean isSuccessful = service.tryUpdateContact(state.getParams(), oldContact);
                if(isSuccessful)
                    response.setText("Контакт " + contactName + " успешно изменен");
                else
                    response.setText("Контакт " + contactName + " не был изменен. Произошла ошибка");
                response.setReplyMarkup(keyboardCreator.currentContactMenu());

                state.changeCurrentOperation(Operation.CURRENT_CONTACT_MENU);
                state.addParameter("currentContact", contactName);
                break;
            case "Назад":
                response.setText("Вы вернулись назад");
                response.setReplyMarkup(keyboardCreator.currentContactMenu());

                state.changeCurrentOperation(Operation.CURRENT_CONTACT_MENU);
                state.addParameter("currentContact", contactName);
                break;
            default:
                return handleMessageWithContext(state, messageText);
        }
        return response;
    }

    private SendMessage handleMessageWithContext(State state, String messageText) {
        SendMessage response = new SendMessage();
        String lastRequestedParamKey = state.getLastRequestedParamKey();
        if (lastRequestedParamKey == null) {
            response.setText("Я не понимаю эту команду.");
            return response;
        }
        state.addParameter(lastRequestedParamKey, messageText);
        response.setText("Отлично. Выберите какие данные хотите изменить у контакта");
        response.setReplyMarkup(new ReplyKeyboardCreator().editContactMenu());
        return response;
    }
}
