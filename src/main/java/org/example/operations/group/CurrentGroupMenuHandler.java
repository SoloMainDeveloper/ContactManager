package org.example.operations.group;

import org.example.entity.Contact;
import org.example.entity.Group;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.GroupService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Обработчик события: действия пользователя в меню текущей группы
 */
@Component
public class CurrentGroupMenuHandler implements OperationHandler {
    /**
     * Создает текст для кнопок быстрого ввода команд
     */
    private final ReplyKeyboardConstants keyboardCreator = new ReplyKeyboardConstants();

    /**
     * Сервис групп
     */
    private final GroupService groupService;

    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Конструктор
     */
    public CurrentGroupMenuHandler(GroupService groupService, StateService stateService) {
        this.groupService = groupService;
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.CURRENT_GROUP_MENU;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        String groupName = stateService.getParamByKey(chatId, "currentGroupName");
        Group group = new Group();
        //TODO groupService.findGroupByName(chatId, groupName).orElse(null);
        if(group == null) {
            response.setText("Группа " + groupName + " не была найдена");
            return response;
        }
        switch (messageText) {
            case "Меню группы вызвано" -> {
                response.setText("Меню для группы " + groupName + " вызвано");
                response.setKeyboardText(keyboardCreator.currentGroupMenu());
            }
            case "Вывести все контакты группы" -> {
                //TODO
                stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU, true);
            }
            case "Изменить" -> {
                stateService.changeCurrentOperation(chatId, Operation.EDIT_GROUP, false);
                //TODO
            }
            case "Удалить" -> {
                stateService.changeCurrentOperation(chatId, Operation.DELETE_GROUP, false);
                response.setText("Вы точно хотите удалить текущую группу?");
                response.setKeyboardText(List.of("Да", "Нет"));
            }
            case "Назад" -> {
                response.setText("Вы вернулись назад");
                stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU, true);
                response.setKeyboardText(keyboardCreator.contactsMenu());
            }
            default -> response.setText("Я не понимаю эту команду");
        }
        return response;
    }
}
