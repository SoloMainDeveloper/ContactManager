package org.example.operations.group;

import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.GroupService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

/**
 * Обработчик события: Удаление группы
 */
@Component
public class DeleteGroupHandler implements OperationHandler {
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
    public DeleteGroupHandler(GroupService groupService, StateService stateService) {
        this.groupService = groupService;
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.DELETE_GROUP;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        switch(messageText) {
            case "Да" -> {
                String contactName = stateService.getParamByKey(chatId,
                        "currentGroupName");
                groupService.deleteGroupByName(chatId, contactName);
                response.setText("Группа " + contactName + " успешно удалена");
                response.setKeyboardText(keyboardCreator.groupsMenu());
                stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU, true);
            }
            case "Нет" -> {
                stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU, true);
                response.setText("Действие удаления текущей группы отменено");
                response.setKeyboardText(keyboardCreator.groupsMenu());
            }
            default -> response.setText("Я не понимаю эту команду.");
        }
        return response;
    }
}
