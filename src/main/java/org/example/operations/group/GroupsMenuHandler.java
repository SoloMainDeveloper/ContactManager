package org.example.operations.group;

import org.example.keyboardcreator.ReplyConstants;
import org.example.keyboardcreator.ReplyKeyboardConstants;
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
     * Конструктор
     */
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
            case "Добавить" -> {
                stateService.changeCurrentOperation(chatId, Operation.ADD_GROUP);
                response.setText("Напишите имя добавляемой группы");
                stateService.setLastRequestedParamKey(chatId, "groupName");
            }
            case "Получить все" -> {
                stateService.changeCurrentOperation(chatId, Operation.GET_ALL_GROUPS);
                response.setText("Желаете получить все группы сразу или"
                        + " добавить сортировку?");
                response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_GROUPS_MENU);
            }
            case "Найти" -> {
                stateService.changeCurrentOperation(chatId, Operation.FIND_GROUP);
                response.setText("Введите имя группы, которую нужно найти");
                stateService.setLastRequestedParamKey(chatId, "groupName");
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
