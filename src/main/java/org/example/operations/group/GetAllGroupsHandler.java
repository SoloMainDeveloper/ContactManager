package org.example.operations.group;

import org.example.entity.Group;
import org.example.keyboardcreator.ReplyConstants;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.response.InlineKeyboardText;
import org.example.service.GroupService;
import org.example.service.StateService;
import org.example.state.Operation;
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

    /**
     * Конструктор
     */
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
            case "Получить" -> {
                List<Group> groups = groupService.findGroupsByChatId(chatId);
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
            case "Сортировать" -> {
                response.setText("Выберите вид сортировки");
                response.setKeyboardText(ReplyKeyboardConstants.ADD_SORTER_GROUP_MENU);
            }
            case "Назад" -> {
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
        if (Objects.equals(messageText, "В алфавитном порядке имени") ||
                Objects.equals(messageText, "В обратном алфавитному порядке имени") ||
                Objects.equals(messageText, "В порядке убывания кол-ва участников") ||
                Objects.equals(messageText, "В порядке возрастания кол-ва участников")) {
            response.setText("Отлично. Выбрана следующая сортировка: " + messageText);
            List<Group> groups = groupService.findGroupsByChatIdWithSorter(
                    chatId, createSorterFromMessageText(messageText));
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
     * Создать сортировку из текста сообщения
     */
    private String createSorterFromMessageText(String message) {
        return switch (message) {
            case "В алфавитном порядке имени" -> " ORDER BY groups.name ASC";
            case "В обратном алфавитному порядке имени" -> " ORDER BY groups.name DESC";
            case "В порядке возрастания кол-ва участников" ->
                    " ORDER BY participants_count ASC, groups.name ASC";
            case "В порядке убывания кол-ва участников" ->
                    " ORDER BY participants_count DESC, groups.name ASC";
            default -> "";
        };
    }
}
