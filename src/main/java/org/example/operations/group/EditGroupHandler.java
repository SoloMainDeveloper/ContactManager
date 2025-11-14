package org.example.operations.group;

import org.example.entity.Contact;
import org.example.entity.Group;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.ContactService;
import org.example.service.GroupService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.example.utils.GroupConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Обработчик события: Редактирование группы
 */
@Component
public class EditGroupHandler implements OperationHandler {
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
    private final ContactService contactService;

    /**
     * Сервис состояний
     */
    private final StateService stateService;

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
                stateService.setLastRequestedParamKey(chatId, "newName");
            }
            case "Удалить контакт из группы" -> {
                response.setText("Введите имя контакта, " +
                        "который вы хотите удалить из группы");
                stateService.setLastRequestedParamKey(chatId, "deleteContact");
            }
            case "Добавить контакт в группу" -> {
                response.setText("Введите имя контакта, " +
                        "который вы хотите добавить в группу");
                stateService.setLastRequestedParamKey(chatId, "addContact");
            }
            case "Сохранить группу" -> {
                applyChangesForGroup(chatId);
                response.setText("Группа успешно отредактирована и сохранена");
                stateService.changeCurrentOperation(chatId, Operation.CURRENT_GROUP_MENU, false);
                response.setKeyboardText(keyboardCreator.currentGroupMenu());
            }
            case "Назад" -> {
                applyChangesForGroup(chatId);
                response.setText("Вы вернулись назад");
                stateService.changeCurrentOperation(chatId, Operation.CURRENT_GROUP_MENU, false);
                response.setKeyboardText(keyboardCreator.currentGroupMenu());
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
            return new BotResponse("Я не понимаю эту команду.");
        }
        Contact contact = contactService
                .findContactByName(chatId, messageText).orElse(null);

        return switch (lastRequestedParamKey) {
            case "newName" -> handleRenameGroup(chatId, messageText);
            case "deleteContact" -> handleDeleteContactFromGroup
                    (chatId, contact);
            case "addContact" -> handleAddContactToGroup(chatId, contact);
            default -> new BotResponse("Я не понимаю эту команду");
        };
    }

    /**
     * Обработать новое имя группы, введенное пользователем
     */
    private BotResponse handleRenameGroup(Long chatId, String newName) {
        BotResponse response = new BotResponse();
        List<Group> groups = groupService.findGroupsByChatId(chatId);
        List<String> groupNames = groups.stream().map(Group::getName).toList();
        if(groupNames.contains(newName)) {
            response.setText("Группа с таким именем уже существует, попробуйте еще раз");
        } else {
            stateService.addParameter(chatId, "newName", newName);
            response.setText("Имя группы записано на обновление.\n" +
                    "Желаете внести ещё изменения в группу?");
        }
        return  response;
    }

    /**
     * Обработать сообщение на удаление контакта из группы
     */
    private BotResponse handleDeleteContactFromGroup(
            Long chatId, Contact contact) {
        BotResponse response = new BotResponse();
        if(contact == null) {
            response.setText("Контакт c введенным именем не был найден");
            return response;
        }
        GroupConverter converter = new GroupConverter();
        Set<Long> contactIds = converter.stringToContactIds(
                stateService.getParamByKey(chatId, "contactIdsToDelete"));
        contactIds.add(contact.getId());
        stateService.addParameter(chatId, "contactIdsToDelete",
                converter.contactIdsToString(contactIds));

        response.setText("Контакт добавлен на удаление.\n" +
                    "Желаете внести ещё изменения в группу?");
        return  response;
    }

    /**
     * Обработать сообщение на добавление контакта в группу
     */
    private BotResponse handleAddContactToGroup(
            Long chatId, Contact contact) {
        BotResponse response = new BotResponse();
        if(contact == null) {
            response.setText("Контакт c введенным именем не был найден");
            return response;
        }
        GroupConverter converter = new GroupConverter();
        Set<Long> contactIds = converter.stringToContactIds(
                stateService.getParamByKey(chatId, "contactIdsToAdd"));
        contactIds.add(contact.getId());
        stateService.addParameter(chatId, "contactIdsToAdd",
                converter.contactIdsToString(contactIds));

        response.setText("Контакт внесен на добавление.\n" +
                "Желаете внести ещё изменения в группу?");
        return  response;
    }

    /**
     * Применить изменения для редактируемой группы
     */
    private void applyChangesForGroup(Long chatId) {
        GroupConverter converter = new GroupConverter();
        String groupName = stateService.getParamByKey(chatId, "currentGroupName");
        String newName = stateService.getParamByKey(chatId, "newName");
        if(newName == null) {
            newName = groupName;
        }
        groupService.tryUpdateGroupWithNewName(chatId, groupName, newName);

        Set<Long> contactIdsToAdd = converter.stringToContactIds(
                stateService.getParamByKey(chatId, "contactIdsToAdd"));
        for(Long contactId : contactIdsToAdd) {
            Contact contact = contactService
                    .findContactById(chatId, contactId).orElse(null);
            groupService.tryAddContactToGroup(chatId, newName, contact);
        }
        Set<Long> contactIdsToDelete = converter.stringToContactIds(
                stateService.getParamByKey(chatId, "contactIdsToDelete"));
        for(Long contactId : contactIdsToDelete) {
            Contact contact = contactService
                    .findContactById(chatId, contactId).orElse(null);
            groupService.tryRemoveContactFromGroup(chatId, newName, contact);
        }
    }
}
