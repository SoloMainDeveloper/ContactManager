package org.example.operations.group;

import org.example.entity.Contact;
import org.example.entity.Group;
import org.example.exceptions.GroupAlreadyExistsException;
import org.example.keyboardcreator.ReplyConstants;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.ContactService;
import org.example.service.GroupService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Обработчик события: Добавление группы
 */
@Component
public class AddGroupHandler implements OperationHandler {
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
     * Название параметра запроса новой группы
     */
    private static final String NEW_GROUP = "newGroup";

    /**
     * Название параметра запроса добавляемого контакта
     */
    private static final String CONTACT_TO_ADD = "contactToAdd";

    /**
     * Конструктор
     */
    public AddGroupHandler(GroupService groupService, ContactService contactService,
                           StateService stateService) {
        this.groupService = groupService;
        this.contactService = contactService;
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.ADD_GROUP;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        switch (messageText) {
            case "Добавить контакт" -> {
                response.setText("Введите имя контакта для добавления в группу");
                stateService.setLastRequestedParamKey(chatId, CONTACT_TO_ADD);
            }
            case "Сохранить группу" -> {
                try {
                    Group newGroup = (Group) stateService.getParamByKey(
                            chatId, NEW_GROUP);
                    groupService.tryAddGroup(chatId, newGroup);
                    response.setText(
                            "Группа " + newGroup.getName() + " успешна сохранена");
                    response.setKeyboardText(ReplyKeyboardConstants.ADD_GROUP_MENU);
                } catch (GroupAlreadyExistsException  e) {
                    e.printStackTrace();
                    response.setText(
                            "Произошла ошибка при добавлении группы: " + e.getMessage());
                }
                response.setKeyboardText(ReplyKeyboardConstants.GROUPS_MENU);
                stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU);
            }
            case "Назад" -> {
                response.setText(ReplyConstants.COME_BACK);
                stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU);
                response.setKeyboardText(ReplyKeyboardConstants.GROUPS_MENU);
            }
            default -> {
                return handleMessageWithContext(chatId, messageText);
            }
        }
        return response;
    }


    /**
     * Обработать сообщение с учётом контекста операции
     */
    private BotResponse handleMessageWithContext(Long chatId, String messageText) {
        String lastRequestedParamKey = stateService.getLastRequestedParamKey(chatId);
        return switch (lastRequestedParamKey) {
            case "groupName" -> handleGroupNameMessage(chatId, messageText);
            case CONTACT_TO_ADD -> handleContactToAddMessage(chatId, messageText);
            default -> new BotResponse(ReplyConstants.UNKNOWN_COMMAND);
        };
    }

    /**
     * Обработать сообщение, содержащее имя добавляемой группы
     */
    private BotResponse handleGroupNameMessage(Long chatId, String groupName) {
        BotResponse response = new BotResponse();
        Group group = new Group(chatId, groupName);
        stateService.addParameter(chatId, NEW_GROUP, group);
        response.setText("Отлично. Выберите дальнейшие действия");
        response.setKeyboardText(ReplyKeyboardConstants.ADD_GROUP_MENU);
        return response;
    }

    /**
     * Обработать сообщение, содержащее имя контакта, добавляемого в группу
     */
    private BotResponse handleContactToAddMessage(Long chatId, String contactName) {
        BotResponse response = new BotResponse();
        Optional<Contact> optionalContact = contactService
                .findContactByName(chatId, contactName);
        if(optionalContact.isEmpty()) {
            response.setText("Контакт " + contactName + " не был найден.");
            return response;
        }

        Contact contact = optionalContact.get();
        Long contactId = contact.getId();
        Group group = (Group) stateService.getParamByKey(chatId, NEW_GROUP);

        List<Contact> contacts = group.getContacts();
        Map<Long, Contact> contactMap = contacts.stream()
            .collect(Collectors.toMap(
                Contact::getId,
                Function.identity()
            ));

        if (contactMap.containsKey(contactId)) {
            response.setText("Контакт с таким именем уже добавлен");
        } else {
            group.addContact(contact);
            response.setText("Контакт " + contactName + " успешно добавлен в " +
                    "группу. Желаете добавить ещё контактов в группу?");
        }
        return response;
    }
}
