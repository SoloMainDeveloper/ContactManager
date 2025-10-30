package org.example.operations;

import org.example.entity.Contact;
import org.example.keyboardcreator.InlineKeyboardCreator;
import org.example.keyboardcreator.ReplyKeyboardCreator;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.Objects;

public class GetAllContactsHandler implements OperationHandler {
    @Override
    public SendMessage handleMessage(ContactService service, State state, String messageText, Long chatId) {
        ReplyKeyboardCreator keyboardCreator = new ReplyKeyboardCreator();
        SendMessage response = new SendMessage();
        switch(messageText){
            case "Получить сразу":
                response.setText("Все контакты");
                List<Contact> contacts = service.findContactsByChatId(chatId);
                List<String> names = contacts.stream()
                        .map(Contact::getName)
                        .toList();
                response.setReplyMarkup(new InlineKeyboardCreator().createKeyboard(names));
                state.setLastRequestedParamKey("currentContact");
                break;
            case "Добавить фильтр":
                response.setText("Выберите один из фильтров");
                response.setReplyMarkup(keyboardCreator.createKeyboard(List.of("По полу", "По возрасту")));
                state.setLastRequestedParamKey("filter");
                break;
            case "Добавить сортировку":
                response.setText("Выберите пол");
                response.setReplyMarkup(keyboardCreator.createKeyboard(List.of("Мужской", "Женский")));
                state.setLastRequestedParamKey("contactGender");
                break;
            case "Назад":
                response.setText("Вы вернулись назад");
                state.changeCurrentOperation(Operation.CONTACTS_MENU);
                response.setReplyMarkup(keyboardCreator.contactsMenu());
                break;
            default:
                return handleMessageWithContext(service, state, messageText, chatId);
        }
        return response;
    }

    private SendMessage handleMessageWithContext(ContactService service, State state, String messageText, Long chatId) {
        ReplyKeyboardCreator keyboardCreator = new ReplyKeyboardCreator();
        SendMessage response = new SendMessage();
        String lastRequestedParamKey = state.getLastRequestedParamKey();
        if (lastRequestedParamKey == null) {
            response.setText("Я не понимаю эту команду.");
            return response;
        }
        state.addParameter(lastRequestedParamKey, messageText);

        switch(lastRequestedParamKey){
            case "currentContact": {
                state.changeCurrentOperation(Operation.CURRENT_CONTACT_MENU);
                state.addParameter(lastRequestedParamKey, messageText);

                String name = state.getParamByKey(lastRequestedParamKey);
                response.setText("Вы находитесь в меню контакта " + name);
                response.setReplyMarkup(new ReplyKeyboardCreator().currentContactMenu());
                break;
            }
            case "filter": {
                if(Objects.equals(messageText, "По полу")) {
                    response.setText("Выберите значение фильтра по полу");
                    //response.setReplyMarkup();
                }
            }
        }

        return response;
    }
}
