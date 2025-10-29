package org.example;

import org.example.operations.OperationHandler;
import org.example.state.State;
import org.example.service.ContactService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Обработчик сообщений от пользователей
 */
public class MessageHandler {
    private final ContactService service = new ContactService();

    /**
     * Получив сообщение, возвращает ответ, содержащий текст и/или кнопки
     */
    public SendMessage handleMessage(State state, Message message){
        long chatId = message.getChatId();
        OperationHandler handler = state.getOperation().getHandler();
        SendMessage response = handler.handleMessage(service, state, message.getText(), chatId);
        response.setChatId(String.valueOf(chatId));
        return response;
    }
}
