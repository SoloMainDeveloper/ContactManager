package org.example.operations.group;

import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

/**
 * Обработчик события: действия пользователя в меню контактов
 */
@Component
public class GroupsMenuHandler implements OperationHandler {
    @Override
    public Operation getSupportedOperation() {
        return Operation.GROUPS_MENU;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        return null;
    }
}
