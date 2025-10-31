package org.example.operations;

import org.example.keyboardcreator.ReplyKeyboardCreator;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

/**
 * Обработчик события: Добавление контакта
 */
public class AddContactHandler implements OperationHandler {
    /**
     * Создает меню из кнопок для быстрого ввода команд
     */
    private final ReplyKeyboardCreator keyboardCreator = new ReplyKeyboardCreator();

    @Override
    public SendMessage handleMessage(ContactService service, State state, String messageText, Long chatId) {
        SendMessage response = new SendMessage();
        switch(messageText){
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
            case "Сохранить контакт":
                boolean isSuccessful = service.tryAddContact(chatId, state.getParams());
                String contactName = state.getParamByKey("contactName");
                if(isSuccessful)
                    response.setText("Контакт " + contactName + " успешно добавлен");
                else
                    response.setText("Контакт " + contactName + " не был добавлен. Произошла ошибка");
                response.setReplyMarkup(keyboardCreator.contactsMenu());
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                break;
            case "Назад":
                response.setText("Вы вернулись назад");
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                response.setReplyMarkup(keyboardCreator.contactsMenu());
                break;
            default:
                return handleMessageWithContext(state, messageText);
        }
        return response;
    }

    /**
     * Обрабатывает сообщение от пользователя. Заполняет контекст входными данными, которые были запрошены ботом
     */
    private SendMessage handleMessageWithContext(State state, String messageText) {
        SendMessage response = new SendMessage();
        String lastRequestedParamKey = state.getLastRequestedParamKey();
        if (lastRequestedParamKey == null) {
            response.setText("Я не понимаю эту команду.");
            return response;
        }
        state.addParameter(lastRequestedParamKey, messageText);
        response.setText("Отлично. Выберите какие данные хотите задать контакту");
        response.setReplyMarkup(keyboardCreator.addContactMenu());
        return response;
    }
}
