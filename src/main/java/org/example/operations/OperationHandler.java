package org.example.operations;

import org.example.service.ContactService;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface OperationHandler {
    SendMessage handleMessage(ContactService service, State state, String messageText, Long chatId);
}
