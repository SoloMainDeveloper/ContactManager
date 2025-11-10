package org.example.operations;

import org.example.response.BotResponse;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.state.Operation;
import org.example.state.State;
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

    @Override
    public Operation getSupportedOperation() {
        return Operation.MAIN_MENU;
    }

    @Override
    public BotResponse handleMessage(State state, String messageText, Long chatId) {
        BotResponse response = new BotResponse();
        switch (messageText) {
            case "/start" -> {
                response.setText("Привет! Я бот для управления контактами.");
                response.setReplyMarkup(keyboardCreator.mainMenu());
            }
            case "Контакты" -> {
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                response.setText("Взаимодействие с контактами. Выберите какое"
                        + " действие хотите совершить");
                response.setReplyMarkup(keyboardCreator.contactsMenu());
            }
            default -> response.setText("Я не понимаю эту команду");
        }
        return response;
    }
}
