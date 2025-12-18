package org.example.operations.group;

import org.example.entity.Group;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.response.InlineKeyboardText;
import org.example.service.GroupService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Обработчик события: Поиск группы
 */
@Component
public class FindGroupHandler implements OperationHandler {
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
    public FindGroupHandler(GroupService groupService, StateService stateService) {
        this.groupService = groupService;
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.FIND_GROUP;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String groupName) {
        BotResponse response = new BotResponse();
        Optional<Group> group = groupService.findGroupByName(chatId, groupName);
        if (group.isPresent()) {
            response.setText("Группа " + groupName + " успешно найдена");
            response.setInlineKeyboardText(new InlineKeyboardText(List.of(groupName),
                    Operation.CURRENT_GROUP_MENU.toString()));
        } else {
            response.setText("По имени " + groupName + " группа не найдена");
        }
        stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU);
        return response;
    }
}
