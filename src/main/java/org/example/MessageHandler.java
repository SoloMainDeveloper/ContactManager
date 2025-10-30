package org.example;

import org.example.operations.OperationHandler;
import org.example.state.Operation;
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
        Long chatId = message.getChatId();
        OperationHandler handler = state.getOperation().getHandler();
        SendMessage response = handler.handleMessage(service, state, message.getText(), chatId);
        response.setChatId(String.valueOf(chatId));
        return response;
    }

    /**
     * Из callBackData достаёт, какую Operation нужно выставить как текущую, а также контекст для этой операции.
     * После этого вызывает обработку сообщения в handleMessage()
     */
    public SendMessage handleCallbackData(String callbackData, State state, Message message) {
        SendMessage response = new SendMessage();
        if (callbackData.startsWith("CURRENT_CONTACT_MENU_")) {
            state.changeCurrentOperation(Operation.CURRENT_CONTACT_MENU, true);
            String contactName = callbackData.substring("CURRENT_CONTACT_MENU_".length());
            state.addParameter("currentContactName", contactName);
            message.setText("Меню пользователя вызвано");
            return handleMessage(state, message);
        }
        return response;
    }
}
