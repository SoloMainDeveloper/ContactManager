package org.example;

import org.example.entity.User;
import org.example.operations.*;
import org.example.response.BotResponse;
import org.example.state.Operation;
import org.example.state.State;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Обработчик сообщений от пользователей
 */
@Component
public class MessageHandler {
    /**
     * Обработчики всех существующих операций в логике бота
     */
    private final Map<Operation, OperationHandler> handlers;

    public MessageHandler(List<OperationHandler> operationHandlers){
        this.handlers = operationHandlers.stream()
                .collect(Collectors.toMap(
                        OperationHandler::getSupportedOperation,
                        Function.identity()
                ));
    }

    /**
     * Получив сообщение, возвращает ответ, содержащий текст и/или кнопки
     */
    public BotResponse handleMessage(User user, String messageText) {
        State state = user.getState();
        OperationHandler handler = handlers.get(state.getOperation());
        if (handler == null) {
            return new BotResponse("Неизвестная операция");
        }
        return handler.handleMessage(state, messageText, user.getChatId());
    }

    /**
     * Из callBackData достаёт, какую Operation нужно выставить как текущую, а также
     * контекст для этой операции. После этого вызывает обработку сообщения в
     * handleMessage()
     * @param callbackData текст, скрытно хранящийся в inline-кнопке, необходимый для
     * обработки действий при нажатии на эту кнопку
     */
    public BotResponse handleInlineButtonActivated(User user, String callbackData) {
        if (callbackData.startsWith("CURRENT_CONTACT_MENU_")) {
            State state = user.getState();
            state.changeCurrentOperation(Operation.CURRENT_CONTACT_MENU, true);
            String contactName = callbackData.substring("CURRENT_CONTACT_MENU_".length());
            state.addParameter("currentContactName", contactName);
            return handleMessage(user, "Меню пользователя вызвано");
        }
        return new BotResponse("Нажатие на inline-кнопку не было обработано");
    }
}
