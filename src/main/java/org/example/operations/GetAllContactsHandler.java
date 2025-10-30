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

/**
 * Обработчик события: Получение всех контактов
 */
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
                response.setReplyMarkup(new InlineKeyboardCreator()
                        .createKeyboard(names, Operation.CURRENT_CONTACT_MENU.toString()));
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
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                response.setReplyMarkup(keyboardCreator.contactsMenu());
                break;
            default:
                return handleMessageWithContext(service, state, messageText, chatId);
        }
        return response;
    }

    /**
     * Обрабатывает сообщение от пользователя. Заполняет контекст входными данными, которые были запрошены ботом, и затем
     * использует их для поиска.
     */
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
