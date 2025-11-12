package org.example.operations.group;

import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

/**
 * Обработчик события: Редактирование группы
 */
@Component
public class EditGroupHandler implements OperationHandler {
    @Override
    public Operation getSupportedOperation() {
        return Operation.EDIT_GROUP;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        return null;
    }
}
