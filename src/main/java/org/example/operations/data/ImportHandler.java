package org.example.operations.data;

import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.ImportService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Обработчик события: импорт контактов
 */
@Component
public class ImportHandler implements OperationHandler {
    /**
     * Создает меню из кнопок для быстрого ввода команд
     */
    private final ReplyKeyboardConstants keyboardCreator = new ReplyKeyboardConstants();

    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Сервис импорта контактов
     */
    private final ImportService importService;

    /**
     * Конструктор
     */
    @Autowired
    public ImportHandler(StateService stateService, ImportService importService){
        this.stateService = stateService;
        this.importService = importService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.IMPORT_CONTACTS;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        return null;
    }
}
