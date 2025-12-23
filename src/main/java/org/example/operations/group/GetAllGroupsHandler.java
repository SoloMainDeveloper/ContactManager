package org.example.operations.group;

import org.example.constants.UserCommandConstants;
import org.example.entity.Group;
import org.example.constants.ReplyConstants;
import org.example.constants.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.response.InlineKeyboardText;
import org.example.service.GroupService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.example.utils.GroupOrder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Обработчик события: Получение всех групп
 */
@Component
public class GetAllGroupsHandler implements OperationHandler {
    /**
     * Сервис групп
     */
    private final GroupService groupService;

    /**
     * Сервис состояний
     */
    private final StateService stateService;

    public GetAllGroupsHandler(GroupService groupService, StateService stateService) {
        this.groupService = groupService;
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.GET_ALL_GROUPS;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        switch (messageText) {
            case UserCommandConstants.GET -> {
                List<Group> groups = groupService
                    .findGroupsByChatId(chatId, new GroupOrder());
                if (groups.isEmpty()) {
                    response.setText("У вас пока нет созданных групп");
                } else {
                    List<String> groupNames = groups.stream()
                        .map(Group::getName)
                        .toList();
                    response.setText("Все ваши группы:");
                    response.setInlineKeyboardText(new InlineKeyboardText(
                        groupNames, Operation.CURRENT_GROUP_MENU.name()));
                }
            }
            case UserCommandConstants.SORT -> {
                response.setText("Выберите вид сортировки");
                response.setKeyboardText(ReplyKeyboardConstants.ADD_SORTER_GROUP_MENU);
            }
            case UserCommandConstants.BACK -> {
                response.setText(ReplyConstants.COME_BACK);
                stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU);
                response.setKeyboardText(ReplyKeyboardConstants.GROUPS_MENU);
            }
            default -> response = handleMessageWithContext(chatId, messageText);
        }
        return response;
    }

    /**
     * Обработать сообщение с учётом контекста операции
     */
    private BotResponse handleMessageWithContext(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        if (Objects.equals(messageText, UserCommandConstants.ORDER_BY_NAME_ASC) ||
            Objects.equals(messageText, UserCommandConstants.ORDER_BY_NAME_DESC) ||
            Objects.equals(messageText,
                UserCommandConstants.ORDER_BY_PARTICIPANTS_COUNT_DESC) ||
            Objects.equals(messageText,
                UserCommandConstants.ORDER_BY_PARTICIPANTS_COUNT_ASC)) {
            response.setText("Отлично. Выбрана следующая сортировка: " + messageText);
            GroupOrder order = createOrderFromMessageText(messageText);
            List<Group> groups = groupService.findGroupsByChatId(chatId, order);
            if (groups.isEmpty()) {
                response.setText("Группы не найдены с примененной фильтрацией");
                response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_GROUPS_MENU);
            } else {
                response.setText("Все группы с выбранной сортировкой:");
                List<String> names = groups.stream()
                    .map(Group::getName)
                    .toList();
                response.setInlineKeyboardText(new InlineKeyboardText(
                    names, Operation.CURRENT_GROUP_MENU.toString()));
            }
        } else if (Objects.equals(messageText, "Назад к выбору")) {
            response.setText(ReplyConstants.COME_BACK);
            response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_GROUPS_MENU);
            stateService.changeCurrentOperation(chatId, Operation.GET_ALL_GROUPS);
        } else {
            response.setText(ReplyConstants.UNKNOWN_COMMAND);
        }
        return response;
    }

    /**
     * Создать порядок сортировки из текста сообщения
     */
    private GroupOrder createOrderFromMessageText(String message) {
        return switch (message) {
            case UserCommandConstants.ORDER_BY_NAME_ASC ->
                new GroupOrder(GroupOrder.OrderProperty.NAME, GroupOrder.Direction.ASC);
            case UserCommandConstants.ORDER_BY_NAME_DESC ->
                new GroupOrder(GroupOrder.OrderProperty.NAME, GroupOrder.Direction.DESC);
            case UserCommandConstants.ORDER_BY_PARTICIPANTS_COUNT_ASC ->
                new GroupOrder(GroupOrder.OrderProperty.COUNT, GroupOrder.Direction.ASC);
            case UserCommandConstants.ORDER_BY_PARTICIPANTS_COUNT_DESC ->
                new GroupOrder(GroupOrder.OrderProperty.COUNT, GroupOrder.Direction.DESC);
            default -> new GroupOrder();
        };
    }
}
