package org.example.operations.group;

import org.example.constants.ReplyConstants;
import org.example.constants.ReplyKeyboardConstants;
import org.example.constants.UserCommandConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

/**
 * Обработчик события: действия пользователя в меню контактов
 */
@Component
public class GroupsMenuHandler implements OperationHandler {
    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Название параметра запроса имени группы
     */
    private static final String GROUP_NAME = "groupName";

    public GroupsMenuHandler(StateService stateService) {
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.GROUPS_MENU;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        switch (messageText) {
            case UserCommandConstants.ADD -> {
                stateService.changeCurrentOperation(chatId, Operation.ADD_GROUP);
                response.setText("Напишите имя добавляемой группы");
                stateService.setLastRequestedParamKey(chatId, GROUP_NAME);
            }
            case UserCommandConstants.GET_ALL -> {
                stateService.changeCurrentOperation(chatId, Operation.GET_ALL_GROUPS);
                response.setText("Желаете получить все группы сразу или"
                    + " добавить сортировку?");
                response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_GROUPS_MENU);
            }
            case UserCommandConstants.FIND -> {
                stateService.changeCurrentOperation(chatId, Operation.FIND_GROUP);
                response.setText("Введите имя группы, которую нужно найти");
                stateService.setLastRequestedParamKey(chatId, GROUP_NAME);
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
