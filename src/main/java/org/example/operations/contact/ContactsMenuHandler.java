package org.example.operations.contact;

import org.example.constants.ReplyConstants;
import org.example.operations.OperationHandler;
import org.example.constants.UserCommandConstants;
import org.example.response.BotResponse;
import org.example.constants.ReplyKeyboardConstants;
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
            case UserCommandConstants.ADD -> {
                stateService.changeCurrentOperation(chatId, Operation.ADD_CONTACT);
                response.setText("Напишите имя добавляемого контакта");
                stateService.setLastRequestedParamKey(chatId, "contactName");
            }
            case UserCommandConstants.GET_ALL -> {
                stateService.changeCurrentOperation(chatId, Operation.GET_ALL_CONTACTS);
                response.setText("Желаете получить все контакты сразу или добавить" +
                    " фильтрацию/сортировку?");
                response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_CONTACTS_MENU);
            }
            case UserCommandConstants.FIND -> {
                stateService.changeCurrentOperation(chatId, Operation.FIND_CONTACT);
                response.setText("Выберите по какому признаку будет произведен поиск");
                response.setKeyboardText(ReplyKeyboardConstants.FIND_CONTACT_MENU);
            }
            case UserCommandConstants.BACK -> {
                response.setText(ReplyConstants.COME_BACK);
                stateService.changeCurrentOperation(chatId, Operation.MAIN_MENU);
                response.setKeyboardText(ReplyKeyboardConstants.MAIN_MENU);
            }
            default -> response.setText(ReplyConstants.UNKNOWN_COMMAND);
        }
        return response;
    }
}
