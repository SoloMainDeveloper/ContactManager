package org.example.operations.group;

import org.example.entity.Contact;
import org.example.entity.Group;
import org.example.keyboardcreator.ReplyConstants;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.response.InlineKeyboardText;
import org.example.service.ContactService;
import org.example.service.GroupService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Обработчик события: действия пользователя в меню текущей группы
 */
@Component
public class CurrentGroupMenuHandler implements OperationHandler {
    /**
     * Сервис групп
     */
    private final GroupService groupService;

    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Конструктор
     */
    public CurrentGroupMenuHandler(GroupService groupService,
                                   ContactService contactService,
                                   StateService stateService) {
        this.groupService = groupService;
        this.contactService = contactService;
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.CURRENT_GROUP_MENU;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        String groupName = (String) stateService.getParamByKey(
                chatId, "currentGroupName");
        Optional<Group> group = groupService.findGroupByName(chatId, groupName);
        if (group.isEmpty()) {
            response.setText("Группа " + groupName + " не была найдена");
            response.setKeyboardText(ReplyKeyboardConstants.GROUPS_MENU);
            stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU);
            return response;
        }
        stateService.addParameter(chatId, "currentGroup", group.get());
        switch (messageText) {
            case "Меню группы вызвано" -> {
                response.setText("Меню для группы " + groupName + " вызвано");
                response.setKeyboardText(ReplyKeyboardConstants.CURRENT_GROUP_MENU);
            }
            case "Вывести все контакты группы" -> {
                response = handleGetAllContactsFromGroup(chatId, group.get());
            }
            case "Изменить" -> {
                stateService.changeCurrentOperation(chatId, Operation.EDIT_GROUP);
                response.setText("Отлично. Выберите какие операции хотите выполнить");
                response.setKeyboardText(ReplyKeyboardConstants.EDIT_GROUP_MENU);
            }
            case "Удалить" -> {
                stateService.changeCurrentOperation(chatId, Operation.DELETE_GROUP);
                response.setText("Вы точно хотите удалить текущую группу?");
                response.setKeyboardText(ReplyKeyboardConstants.YES_NO);
            }
            case "Назад" -> {
                response.setText(ReplyConstants.COME_BACK);
                stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU);
                response.setKeyboardText(ReplyKeyboardConstants.CONTACTS_MENU);
            }
            default -> response.setText(ReplyConstants.UNKNOWN_COMMAND);
        }
        return response;
    }

    /**
     * Обработать сообщение по выводу всех контактов группы
     */
    private BotResponse handleGetAllContactsFromGroup(Long chatId, Group group) {
        BotResponse response = new BotResponse();

        List<String> contactNames = new ArrayList<>();
        for (Contact contact : group.getContacts()) {
            contactService.findContactById(chatId, contact.getId())
                    .ifPresent(c ->
                            contactNames.add(c.getName())
                    );
        }
        if (contactNames.isEmpty()) {
            response.setText("В группе " + group.getName() + " пока нет контактов");
        } else {
            response.setText("Все контакты группы " + group.getName() + ":");
            response.setInlineKeyboardText(new InlineKeyboardText(contactNames,
                    Operation.CURRENT_CONTACT_MENU.name()));
        }
        return response;
    }
}
