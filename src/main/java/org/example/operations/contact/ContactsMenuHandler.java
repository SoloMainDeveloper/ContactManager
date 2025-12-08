package org.example.operations.contact;

import org.example.keyboardcreator.ReplyConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

/**
 * Обработчик события: действия пользователя в меню контактов
 */
@Component
public class ContactsMenuHandler implements OperationHandler {
    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Конструктор
     */
    public ContactsMenuHandler(StateService stateService) {
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.CONTACTS_MENU;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        switch (messageText) {
            case "Добавить" -> {
                stateService.changeCurrentOperation(chatId, Operation.ADD_CONTACT);
                response.setText("Напишите имя добавляемого контакта");
                stateService.setLastRequestedParamKey(chatId, "contactName");
            }
            case "Получить все" -> {
                stateService.changeCurrentOperation(chatId, Operation.GET_ALL_CONTACTS);
                response.setText("Желаете получить все контакты сразу или добавить" +
                        " фильтрацию/сортировку?");
                response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_CONTACTS_MENU);
            }
            case "Найти" -> {
                stateService.changeCurrentOperation(chatId, Operation.FIND_CONTACT);
                response.setText("Выберите по какому признаку будет произведен поиск");
                response.setKeyboardText(ReplyKeyboardConstants.FIND_CONTACT_MENU);
            }
            case "Назад" -> {
                response.setText(ReplyConstants.COME_BACK);
                stateService.changeCurrentOperation(chatId, Operation.MAIN_MENU);
                response.setKeyboardText(ReplyKeyboardConstants.MAIN_MENU);
            }
            default -> response.setText(ReplyConstants.UNKNOWN_COMMAND);
        }
        return response;
    }
}
