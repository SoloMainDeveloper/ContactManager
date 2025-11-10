package org.example.operations;

import org.example.response.BotResponse;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.state.Operation;
import org.example.state.State;
import org.springframework.stereotype.Component;

/**
 * Обработчик события: действия пользователя в меню контактов
 */
@Component
public class ContactsMenuHandler implements OperationHandler {
    /**
     * Создает меню из кнопок для быстрого ввода команд
     */
    private final ReplyKeyboardConstants keyboardCreator = new ReplyKeyboardConstants();

    @Override
    public Operation getSupportedOperation() {
        return Operation.CONTACTS_MENU;
    }

    @Override
    public BotResponse handleMessage(State state, String messageText, Long chatId) {
        BotResponse response = new BotResponse();
        switch (messageText) {
            case "Добавить":
                state.changeCurrentOperation(Operation.ADD_CONTACT, true);
                response.setText("Напишите имя добавляемого контакта");
                state.setLastRequestedParamKey("contactName");
                break;
            case "Получить все":
                state.changeCurrentOperation(
                        Operation.GET_ALL_CONTACTS, true);
                response.setText("Желаете получить все контакты сразу или добавить" +
                        " фильтрацию/сортировку?");
                response.setReplyMarkup(keyboardCreator.getAllContactsMenu());
                break;
            case "Найти":
                state.changeCurrentOperation(Operation.FIND_CONTACT, true);
                response.setText("Выберите по какому признаку будет произведен поиск");
                response.setReplyMarkup(keyboardCreator.findContactMenu());
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
