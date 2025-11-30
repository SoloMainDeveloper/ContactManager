package org.example.operations.data;

import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Обработчик события: действия пользователя в меню Данные
 */
@Component
public class DataMenuHandler implements OperationHandler {
    /**
     * Создает меню из кнопок для быстрого ввода команд
     */
    private final ReplyKeyboardConstants keyboardCreator = new ReplyKeyboardConstants();

    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Конструктор
     */
    @Autowired
    public DataMenuHandler(StateService stateService){
        this.stateService = stateService;
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
                stateService.changeCurrentOperation(
                        chatId, Operation.IMPORT_CONTACTS, true);
                response.setText("Отправьте файл с данными для импорта");
                stateService.setLastRequestedParamKey(chatId, "importData");
            }
            case "Экспорт контактов" -> {
                stateService.changeCurrentOperation(
                        chatId, Operation.EXPORT_CONTACTS, true);
                response.setText("Выберите желаемый формат экспорта контактов");
                response.setKeyboardText(List.of()); //как нибудь придумать чтобы прям
                // из ExportService.getSupportableFormats()
            }
            case "Назад" -> {
                stateService.changeCurrentOperation(chatId, Operation.MAIN_MENU, true);
                response.setText("Вы вернулись назад");
                response.setKeyboardText(keyboardCreator.mainMenu());
            }
            default -> response.setText("Я не понимаю эту команду");
        }
        return response;
    }
}
