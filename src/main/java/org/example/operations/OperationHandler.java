package org.example.operations;

import org.example.service.ContactService;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Обработчик операций
 */
public interface OperationHandler {
    /**
     * Обрабатывает сообщение от пользователя в рамках текущего состояния и возвращает ответ
     */
    SendMessage handleMessage(ContactService service, State state, String messageText, Long chatId);
}
