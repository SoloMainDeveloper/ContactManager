package org.example.operations.group;

import org.example.entity.Contact;
import org.example.entity.Group;
import org.example.exceptions.GroupDoesNotExistException;
import org.example.exceptions.GroupEditException;
import org.example.keyboardcreator.ReplyConstants;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.ContactService;
import org.example.service.GroupService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.example.utils.GroupOrder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Обработчик события: Редактирование группы
 */
@Component
public class EditGroupHandler implements OperationHandler {
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
     * Название параметра запроса нового имени группы
     */
    private static final String NEW_NAME = "newName";

    /**
     * Название параметра запроса удаления контакта из группы
     */
    private static final String DELETE_CONTACT = "deleteContact";

    /**
     * Название параметра запроса добавления контакта в группу
     */
    private static final String ADD_CONTACT = "addContact";

    /**
     * Название параметра запроса текущей группы
     */
    private static final String CURRENT_GROUP = "currentGroup";

    /**
     * Конструктор
     */
    public EditGroupHandler(GroupService groupService,
                            ContactService contactService,
                            StateService stateService) {
        this.groupService = groupService;
        this.contactService = contactService;
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.EDIT_GROUP;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        switch (messageText) {
            case "Изменить имя группы" -> {
                response.setText("Введите новое имя группы");
                stateService.setLastRequestedParamKey(chatId, NEW_NAME);
            }
            case "Удалить контакт из группы" -> {
                response.setText("Введите имя контакта, " +
                        "который вы хотите удалить из группы");
                stateService.setLastRequestedParamKey(chatId, DELETE_CONTACT);
            }
            case "Добавить контакт в группу" -> {
                response.setText("Введите имя контакта, " +
                        "который вы хотите добавить в группу");
                stateService.setLastRequestedParamKey(chatId, ADD_CONTACT);
            }
            case "Сохранить группу" -> {
                try {
                    applyChangesForGroup(chatId);
                    response.setText("Группа успешно отредактирована и сохранена");
                } catch (GroupEditException e) {
                    e.printStackTrace();
                    response.setText(e.getMessage());
                }
                stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU);
                response.setKeyboardText(ReplyKeyboardConstants.GROUPS_MENU);
            }
            case "Назад" -> {
                response.setText(ReplyConstants.COME_BACK);
                stateService.changeCurrentOperation(chatId, Operation.CURRENT_GROUP_MENU);
                response.setKeyboardText(ReplyKeyboardConstants.CURRENT_GROUP_MENU);
            }
            default -> response = handleMessageWithContext(chatId, messageText);
        }
        return response;
    }

    /**
     * Обработать сообщение с учётом контекста операции
     */
    private BotResponse handleMessageWithContext(Long chatId, String messageText) {
        String lastRequestedParamKey = stateService.getLastRequestedParamKey(chatId);
        if (lastRequestedParamKey == null) {
            return new BotResponse(ReplyConstants.UNKNOWN_COMMAND);
        }

        return switch (lastRequestedParamKey) {
            case NEW_NAME -> handleRenameGroup(chatId, messageText);
            case DELETE_CONTACT -> handleDeleteContactFromGroup(chatId, messageText);
            case ADD_CONTACT -> handleAddContactToGroup(chatId, messageText);
            default -> new BotResponse(ReplyConstants.UNKNOWN_COMMAND);
        };
    }

    /**
     * Обработать новое имя группы, введенное пользователем
     */
    private BotResponse handleRenameGroup(Long chatId, String newName) {
        BotResponse response = new BotResponse();
        List<Group> groups = groupService.findGroupsByChatId(chatId, GroupOrder.none());
        List<String> groupNames = groups.stream().map(Group::getName).toList();
        if (groupNames.contains(newName)) {
            response.setText("Группа с таким именем уже существует, попробуйте еще раз");
        } else {
            Group group = (Group) stateService.getParamByKey(chatId, CURRENT_GROUP);
            group.setName(newName);
            response.setText("Имя группы записано на обновление.\n" +
                    "Желаете внести ещё изменения в группу?");
        }
        return response;
    }

    /**
     * Обработать сообщение на удаление контакта из группы
     */
    private BotResponse handleDeleteContactFromGroup(Long chatId, String contactName) {
        BotResponse response = new BotResponse();
        Optional<Contact> contact = contactService.findContactByName(chatId, contactName);
        if (contact.isEmpty()) {
            response.setText("Контакт c введенным именем не был найден");
            return response;
        }
        Group group = (Group) stateService.getParamByKey(chatId, CURRENT_GROUP);
        group.removeContact(contact.get());
        response.setText("Контакт добавлен на удаление.\n" +
                "Желаете внести ещё изменения в группу?");
        return response;
    }

    /**
     * Обработать сообщение на добавление контакта в группу
     */
    private BotResponse handleAddContactToGroup(Long chatId, String contactName) {
        BotResponse response = new BotResponse();
        Optional<Contact> contact = contactService.findContactByName(chatId, contactName);
        if (contact.isEmpty()) {
            response.setText("Контакт c введенным именем не был найден");
            return response;
        }
        Group group = (Group) stateService.getParamByKey(chatId, CURRENT_GROUP);
        group.addContact(contact.get());
        response.setText("Контакт внесен на добавление.\n" +
                "Желаете внести ещё изменения в группу?");
        return response;
    }

    /**
     * Применить изменения для редактируемой группы
     *
     * @param chatId идентификатор чата
     * @throws GroupEditException если группа не существует
     */
    private void applyChangesForGroup(Long chatId) throws GroupEditException {
        String groupName = (String) stateService.getParamByKey(
                chatId, "currentGroupName");
        try {
            Group group = (Group) stateService.getParamByKey(chatId, CURRENT_GROUP);
            groupService.tryUpdateGroup(chatId, groupName, group);
        } catch (GroupDoesNotExistException e) {
            throw new GroupEditException(
                    "Произошла ошибка при редактировании группы: " + e.getMessage());
        }
    }
}
