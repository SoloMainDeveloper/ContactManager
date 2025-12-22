package org.example;

import org.example.constants.UserCommandConstants;
import org.example.operations.*;
import org.example.response.BotResponse;
import org.example.service.StateService;
import org.example.state.Operation;
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

    private final StateService stateService;

    public MessageHandler(List<OperationHandler> operationHandlers,
                          StateService stateService) {
        this.handlers = operationHandlers.stream()
                .collect(Collectors.toMap(
                        OperationHandler::getSupportedOperation,
                        Function.identity()
                ));
        this.stateService = stateService;
    }

    /**
     * Получив сообщение, возвращает ответ, содержащий текст и/или кнопки
     */
    public BotResponse handleMessage(Long chatId, String messageText) {
        OperationHandler handler = handlers.get(stateService.getOperation(chatId));
        if (handler == null) {
            return new BotResponse("Неизвестная операция");
        }
        return handler.handleMessage(chatId, messageText);
    }

    /**
     * Из callBackData достаёт, какую Operation нужно выставить как текущую, а также
     * контекст для этой операции. После этого вызывает обработку сообщения в
     * handleMessage()
     *
     * @param callbackData текст, скрытно хранящийся в inline-кнопке, необходимый для
     *                     обработки действий при нажатии на эту кнопку
     */
    public BotResponse handleInlineButtonActivated(Long chatId, String callbackData) {
        if (callbackData.startsWith("CURRENT_CONTACT_MENU_")) {
            stateService.changeCurrentOperation(chatId, Operation.CURRENT_CONTACT_MENU);
            String contactName = callbackData.substring("CURRENT_CONTACT_MENU_".length());
            stateService.addParameter(chatId, "currentContactName", contactName);
            return handleMessage(chatId, UserCommandConstants.CONTACT_MENU);
        } else if (callbackData.startsWith("CURRENT_GROUP_MENU_")) {
            stateService.changeCurrentOperation(chatId, Operation.CURRENT_GROUP_MENU);
            String groupName = callbackData.substring("CURRENT_GROUP_MENU_".length());
            stateService.addParameter(chatId, "currentGroupName", groupName);
            return handleMessage(chatId, UserCommandConstants.GROUP_MENU);
        }
        return new BotResponse("Нажатие на inline-кнопку не было обработано");
    }
}
