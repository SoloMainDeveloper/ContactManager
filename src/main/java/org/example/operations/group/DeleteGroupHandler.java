package org.example.operations.group;

import org.example.constants.UserCommandConstants;
import org.example.exceptions.GroupDoesNotExistException;
import org.example.constants.ReplyConstants;
import org.example.constants.ReplyKeyboardConstants;
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
     * Сервис групп
     */
    private final GroupService groupService;

    /**
     * Сервис состояний
     */
    private final StateService stateService;

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
        switch (messageText) {
            case UserCommandConstants.YES -> {
                try {
                    String contactName = (String) stateService.getParamByKey(chatId,
                        "currentGroupName");
                    groupService.deleteGroupByName(chatId, contactName);
                    response.setText("Группа " + contactName + " успешно удалена");
                } catch (GroupDoesNotExistException e) {
                    response.setText("Ошибка при удалении группы: " + e.getMessage());
                }

                response.setKeyboardText(ReplyKeyboardConstants.GROUPS_MENU);
                stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU);
            }
            case UserCommandConstants.NO -> {
                stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU);
                response.setText("Действие удаления текущей группы отменено");
                response.setKeyboardText(ReplyKeyboardConstants.GROUPS_MENU);
            }
            default -> response.setText(ReplyConstants.UNKNOWN_COMMAND);
        }
        return response;
    }
}
