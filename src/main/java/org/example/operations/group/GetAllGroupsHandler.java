package org.example.operations.group;

import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.GroupService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

/**
 * Обработчик события: Получение всех групп
 */
@Component
public class GetAllGroupsHandler implements OperationHandler {
    /**
     * Создает текст для кнопок быстрого ввода команд
     */
    private final ReplyKeyboardConstants keyboardCreator = new ReplyKeyboardConstants();

    /**
     * Сервис групп
     */
    private final GroupService groupService;

    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Конструктор
     */
    public GetAllGroupsHandler(GroupService groupService, StateService stateService) {
        this.groupService = groupService;
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.GET_ALL_GROUPS;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        return null;
    }
}
