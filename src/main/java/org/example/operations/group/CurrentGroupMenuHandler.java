package org.example.operations.group;

import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

/**
 * Обработчик события: действия пользователя в меню текущей группы
 */
@Component
public class CurrentGroupMenuHandler implements OperationHandler {
    @Override
    public Operation getSupportedOperation() {
        return Operation.CURRENT_GROUP_MENU;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        return null;
    }
}
