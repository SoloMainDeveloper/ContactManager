package org.example.operations.data;

import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.ExportService;
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
            case "Импорт контактов" -> {
                stateService.changeCurrentOperation(chatId, Operation.IMPORT_CONTACTS);
                response.setText("Отправьте файл с данными для импорта");
                stateService.setLastRequestedParamKey(chatId, "importData");
            }
            case "Экспорт контактов" -> {
                stateService.changeCurrentOperation(chatId, Operation.EXPORT_CONTACTS);
                response.setText("Выберите желаемый формат экспорта контактов");
                List<String> keyboardText = new ArrayList<>(exportService
                        .getSupportedFormats()
                        .stream()
                        .toList());
                keyboardText.add("Назад");
                response.setKeyboardText(keyboardText);
                stateService.setLastRequestedParamKey(chatId, "exportFormat");
            }
            case "Назад" -> {
                stateService.changeCurrentOperation(chatId, Operation.MAIN_MENU);
                response.setText("Вы вернулись назад");
                response.setKeyboardText(ReplyKeyboardConstants.MAIN_MENU);
            }
            default -> response.setText("Я не понимаю эту команду");
        }
        return response;
    }
}
