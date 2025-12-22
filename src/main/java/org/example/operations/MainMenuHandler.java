package org.example.operations;

import org.example.constants.ReplyConstants;
import org.example.constants.UserCommandConstants;
import org.example.response.BotResponse;
import org.example.constants.ReplyKeyboardConstants;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

/**
 * Обработчик события: действия пользователя в главном меню
 */
@Component
public class MainMenuHandler implements OperationHandler {
    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Конструктор
     */
    public MainMenuHandler(StateService stateService) {
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.MAIN_MENU;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        switch (messageText) {
            case "/start" -> {
                response.setText("Привет! Я бот для управления контактами.");
                response.setKeyboardText(ReplyKeyboardConstants.MAIN_MENU);
            }
            case UserCommandConstants.CONTACTS -> {
                stateService.changeCurrentOperation(chatId, Operation.CONTACTS_MENU);
                response.setText("Взаимодействие с контактами. Выберите какое"
                        + " действие хотите совершить");
                response.setKeyboardText(ReplyKeyboardConstants.CONTACTS_MENU);
            }
            case UserCommandConstants.GROUPS -> {
                stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU);
                response.setText("Взаимодействие с группами. Выберите какое"
                        + " действие хотите совершить");
                response.setKeyboardText(ReplyKeyboardConstants.GROUPS_MENU);
            }
            case "Данные" -> {
                stateService.changeCurrentOperation(chatId, Operation.DATA_MENU);
                response.setText("Взаимодействие с данными. Выберите какое"
                        + " действие хотите совершить");
                response.setKeyboardText(ReplyKeyboardConstants.DATA_MENU);
            }
            default -> response.setText(ReplyConstants.UNKNOWN_COMMAND);
        }
        return response;
    }
}
