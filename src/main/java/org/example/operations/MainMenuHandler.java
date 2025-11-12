package org.example.operations;

import org.example.response.BotResponse;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

/**
 * Обработчик события: действия пользователя в главном меню
 */
@Component
public class MainMenuHandler implements OperationHandler {
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
    public MainMenuHandler(StateService stateService){
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
                response.setKeyboardText(keyboardCreator.mainMenu());
            }
            case "Контакты" -> {
                stateService.changeCurrentOperation(
                        chatId, Operation.CONTACTS_MENU, true);
                response.setText("Взаимодействие с контактами. Выберите какое"
                        + " действие хотите совершить");
                response.setKeyboardText(keyboardCreator.contactsMenu());
            }
            case "Группы" -> {
                stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU, true);
                response.setText("Взаимодействие с группами. Выберите какое"
                        + " действие хотите совершить");
                response.setKeyboardText(keyboardCreator.groupsMenu());
            }
            default -> response.setText("Я не понимаю эту команду");
        }
        return response;
    }
}
