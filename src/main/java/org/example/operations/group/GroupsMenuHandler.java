package org.example.operations.group;

import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.GroupService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

/**
 * Обработчик события: действия пользователя в меню контактов
 */
@Component
public class GroupsMenuHandler implements OperationHandler {
    /**
     * Создает текст для кнопок быстрого ввода команд
     */
    private final ReplyKeyboardConstants keyboardCreator = new ReplyKeyboardConstants();

    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Конструктор
     */
    public GroupsMenuHandler(StateService stateService) {
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.GROUPS_MENU;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        switch (messageText) {
            case "Добавить" -> {
                stateService.changeCurrentOperation(chatId, Operation.ADD_GROUP, true);
                response.setText("Напишите имя добавляемой группы");
                stateService.setLastRequestedParamKey(chatId, "groupName");
            }
            case "Получить все" -> {
                stateService.changeCurrentOperation(
                        chatId, Operation.GET_ALL_CONTACTS, true);
                response.setText("Желаете получить все группы сразу или"
                        + " добавить сортировку?");
                response.setKeyboardText(keyboardCreator.getAllGroupsMenu());
            }
            case "Найти" -> {
                stateService.changeCurrentOperation(chatId, Operation.FIND_GROUP, true);
                response.setText("Введите имя группы, которую нужно найти");
                stateService.setLastRequestedParamKey(chatId, "groupName");
            }
            case "Назад" -> {
                response.setText("Вы вернулись назад");
                stateService.changeCurrentOperation(chatId, Operation.MAIN_MENU, true);
                response.setKeyboardText(keyboardCreator.mainMenu());
            }
            default -> response.setText("Я не понимаю эту команду");
        }
        return response;
    }
}
