package org.example.operations.data;

import org.example.constants.ReplyConstants;
import org.example.constants.ReplyKeyboardConstants;
import org.example.constants.UserCommandConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.export.ExportService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Обработчик события: действия пользователя в меню Данные
 */
@Component
public class DataMenuHandler implements OperationHandler {
    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Сервис экспорта
     */
    private final ExportService exportService;

    /**
     * Конструктор
     */
    @Autowired
    public DataMenuHandler(StateService stateService, ExportService exportService){
        this.stateService = stateService;
        this.exportService = exportService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.DATA_MENU;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        switch (messageText) {
            case UserCommandConstants.IMPORT_CONTACTS -> {
                stateService.changeCurrentOperation(chatId, Operation.IMPORT_CONTACTS);
                response.setText("Отправьте файл с данными для импорта");
                stateService.setLastRequestedParamKey(chatId, "importData");
            }
            case UserCommandConstants.EXPORT_CONTACTS -> {
                stateService.changeCurrentOperation(chatId, Operation.EXPORT_CONTACTS);
                response.setText("Выберите желаемый формат экспорта контактов");
                List<String> keyboardText = new ArrayList<>(exportService
                        .getSupportedFormats()
                        .stream()
                        .toList());
                keyboardText.add(UserCommandConstants.BACK);
                response.setKeyboardText(keyboardText);
                stateService.setLastRequestedParamKey(chatId, "exportFormat");
            }
            case UserCommandConstants.BACK -> {
                stateService.changeCurrentOperation(chatId, Operation.MAIN_MENU);
                response.setText(ReplyConstants.COME_BACK);
                response.setKeyboardText(ReplyKeyboardConstants.MAIN_MENU);
            }
            default -> response.setText(ReplyConstants.UNKNOWN_COMMAND);
        }
        return response;
    }
}
