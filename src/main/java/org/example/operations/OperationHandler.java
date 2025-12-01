package org.example.operations;

import org.example.response.BotResponse;
import org.example.state.Operation;

/**
 * Обработчик операций
 */
public interface OperationHandler {
    /**
     * Возвращает тип поддерживаемой операции
     */
    Operation getSupportedOperation();

    /**
     * Обрабатывает сообщение от пользователя в рамках текущего состояния и возвращает ответ
     */
    BotResponse handleMessage(Long chatId, String messageText);
}
