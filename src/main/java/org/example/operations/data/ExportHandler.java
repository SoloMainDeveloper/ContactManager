package org.example.operations.data;

import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.ExportService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Обработчик события: экспорт контактов
 */
@Component
public class ExportHandler implements OperationHandler {
    /**
     * Создает меню из кнопок для быстрого ввода команд
     */
    private final ReplyKeyboardConstants keyboardCreator = new ReplyKeyboardConstants();

    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Сервис экспорта контактов
     */
    private final ExportService exportService;

    /**
     * Конструктор
     */
    @Autowired
    public ExportHandler(StateService stateService, ExportService exportService){
        this.stateService = stateService;
        this.exportService = exportService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.EXPORT_CONTACTS;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        return null;
    }
}
