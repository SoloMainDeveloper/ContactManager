package org.example.operations.group;

import org.example.entity.Contact;
import org.example.exceptions.GroupAlreadyExistsException;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.ContactService;
import org.example.service.GroupService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.example.utils.converter.GroupConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

/**
 * Обработчик события: Добавление группы
 */
@Component
public class AddGroupHandler implements OperationHandler {
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
        switch (messageText){
            case "Добавить контакт" -> {
                response.setText("Введите имя контакта для добавления в группу");
                stateService.setLastRequestedParamKey(chatId, "contactToAdd");
            }
            case "Сохранить группу" -> {
                String groupName = stateService.getParamByKey(chatId, "groupName");
                try {
                    groupService.tryAddGroup(chatId, stateService.getParams(chatId));
                    response.setText("Группа " + groupName + " успешна сохранена");
                    response.setKeyboardText(keyboardCreator.addGroupMenu());
                } catch (GroupAlreadyExistsException e) {
                    e.printStackTrace();
                    response.setText(
                            "Произошла ошибка при добавлении группы: " + e.getMessage());
                }
                response.setKeyboardText(keyboardCreator.groupsMenu());
                stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU, true);
            }
            case "Назад" -> {
                response.setText("Вы вернулись назад");
                stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU, true);
                response.setKeyboardText(keyboardCreator.groupsMenu());
            }
            default -> {
                return handleMessageWithContext(chatId, messageText);
            }
        }
        return  response;
    }


    /**
     * Обработать сообщение с учётом контекста операции
     */
    private BotResponse handleMessageWithContext(Long chatId, String messageText) {
         String lastRequestedParamKey = stateService.getLastRequestedParamKey(chatId);
         return switch(lastRequestedParamKey) {
            case "groupName" -> handleGroupNameMessage(chatId, messageText);
            case "contactToAdd" -> handleContactToAddMessage(chatId, messageText);
            default -> new BotResponse("Я не понимаю эту команду.");
        };
    }

    /**
     * Обработать сообщение, содержащее имя добавляемой группы
     */
    private BotResponse handleGroupNameMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        stateService.addParameter(chatId, "groupName", messageText);
        response.setText("Отлично. Выберите дальнейшие действия");
        response.setKeyboardText(keyboardCreator.addGroupMenu());
        return response;
    }

    /**
     * Обработать сообщение, содержащее имя контакта, добавляемого в группу
     */
    private BotResponse handleContactToAddMessage(Long chatId, String contactName) {
        BotResponse response = new BotResponse();
        Optional<Contact> contact = contactService.findContactByName(chatId, contactName);
        GroupConverter converter = new GroupConverter();
        Set<Long> contactIds = converter.stringToContactIds(
                stateService.getParamByKey(chatId, "contactIds"));
        if(contact.isPresent()) {
            Long contactId = contact.get().getId();
            if(contactIds.contains(contactId)) {
                response.setText("Контакт с таким именем уже добавлен");
            } else {
                contactIds.add(contactId);
                stateService.addParameter(chatId, "contactIds",
                        converter.contactIdsToString(contactIds));
                response.setText("Контакт " + contactName + " успешно добавлен в " +
                        "группу. Желаете добавить ещё контактов в группу?");
            }
        } else {
            response.setText("Контакт " + contactName + " не был найден.");
        }
        return response;
    }
}
