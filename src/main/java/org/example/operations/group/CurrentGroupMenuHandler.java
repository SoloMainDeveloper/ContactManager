package org.example.operations.group;

import org.example.entity.Group;
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
     * Сервис контактов
     */
    private  final ContactService contactService;

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
        String groupName = stateService.getParamByKey(chatId, "currentGroupName");
        Group group = groupService.findGroupByName(chatId, groupName).orElse(null);
        if(group == null) {
            response.setText("Группа " + groupName + " не была найдена");
            response.setKeyboardText(keyboardCreator.groupsMenu());
            stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU, true);
            return response;
        }
        switch (messageText) {
            case "Меню группы вызвано" -> {
                response.setText("Меню для группы " + groupName + " вызвано");
                response.setKeyboardText(keyboardCreator.currentGroupMenu());
            }
            case "Вывести все контакты группы" -> {
                response = handleGetAllContactsFromGroup(chatId, group);
            }
            case "Изменить" -> {
                stateService.changeCurrentOperation(chatId, Operation.EDIT_GROUP, false);
                response.setText("Отлично. Выберите какие операции хотите выполнить");
                response.setKeyboardText(keyboardCreator.editGroupMenu());
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

    /**
     * Обработать сообщение по выводу всех контактов группы
     */
    private BotResponse handleGetAllContactsFromGroup(Long chatId, Group group) {
        BotResponse response = new BotResponse();

        List<String> contactNames = new ArrayList<>();
        for(Long contactId : group.getContactIds()) {
            contactService.findContactById(chatId, contactId)
                    .ifPresent(contact ->
                            contactNames.add(contact.getName())
                    );
        }
        if(contactNames.isEmpty()) {
            response.setText("В группе " + group.getName() + " пока нет контактов");
        } else {
            response.setText("Все контакты группы " + group.getName() + ":");
            response.setInlineKeyboardText(new InlineKeyboardText(contactNames,
                    Operation.CURRENT_CONTACT_MENU.name()));
        }
        return response;
    }
}
