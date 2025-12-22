package org.example.operations.contact;

import org.example.constants.ReplyConstants;
import org.example.operations.OperationHandler;
import org.example.constants.UserCommandConstants;
import org.example.response.BotResponse;
import org.example.response.InlineKeyboardText;
import org.example.keyboardcreator.InlineKeyboardCreator;
import org.example.constants.ReplyKeyboardConstants;
import org.example.entity.Contact;
import org.example.service.ContactService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Обработчик события: Поиск контакта
 */
@Component
public class FindContactHandler implements OperationHandler {
    /**
     * Создает кнопки в ответе текста сообщения
     */
    private final InlineKeyboardCreator inlineKeyboardCreator = new InlineKeyboardCreator();

    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Название параметра запроса имени контакта
     */
    private static final String CONTACT_NAME = "contactName";

    /**
     * Название параметра запроса номера контакта
     */
    private static final String CONTACT_NUMBER = "contactNumber";

    /**
     * Конструктор
     */
    public FindContactHandler(ContactService contactService, StateService stateService) {
        this.contactService = contactService;
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.FIND_CONTACT;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        switch (messageText) {
            case UserCommandConstants.FIND_BY_NAME -> {
                response.setText("Введите имя");
                stateService.setLastRequestedParamKey(chatId, CONTACT_NAME);
            }
            case UserCommandConstants.FIND_BY_NUMBER -> {
                response.setText("Введите номер");
                stateService.setLastRequestedParamKey(chatId, CONTACT_NUMBER);
            }
            case UserCommandConstants.BACK -> {
                response.setText(ReplyConstants.COME_BACK);
                stateService.changeCurrentOperation(chatId, Operation.CONTACTS_MENU);
                response.setKeyboardText(ReplyKeyboardConstants.CONTACTS_MENU);
            }
            default -> {
                return handleMessageWithContext(chatId, messageText);
            }
        }
        return response;
    }

    /**
     * Обрабатывает сообщение от пользователя. Заполняет контекст входными данными, которые были запрошены ботом, и затем
     * использует их при поиске.
     */
    private BotResponse handleMessageWithContext(Long chatId, String messageText) {
        String lastRequestedParamKey = stateService.getLastRequestedParamKey(chatId);
        if (lastRequestedParamKey == null) {
            return new BotResponse(ReplyConstants.UNKNOWN_COMMAND);
        }
        stateService.addParameter(chatId, lastRequestedParamKey, messageText);

        return switch (lastRequestedParamKey) {
            case CONTACT_NAME -> handleFindContactByName(chatId);
            case CONTACT_NUMBER -> handleFindContactByPhoneNumber(chatId);
            default -> new BotResponse(ReplyConstants.UNKNOWN_COMMAND);
        };
    }

    /**
     * Обрабатывает команду нахождения контакта по имени
     */
    private BotResponse handleFindContactByName(Long chatId) {
        BotResponse response = new BotResponse();
        String name = (String) stateService.getParamByKey(chatId, CONTACT_NAME);
        Optional<Contact> contact = contactService.findContactByName(chatId, name);

        if (contact.isPresent()) {
            response.setText("По имени " + name + " контакт успешно найден.");
            response.setInlineKeyboardText(new InlineKeyboardText(
                    List.of(contact.get().getName()),
                    Operation.CURRENT_CONTACT_MENU.toString()));
        } else {
            stateService.changeCurrentOperation(chatId, Operation.CONTACTS_MENU);
            response.setText("По имени " + name + " контакты не найдены.");
            response.setKeyboardText(ReplyKeyboardConstants.CONTACTS_MENU);
        }
        return response;
    }

    /**
     * Обрабатывает команду нахождения контакта номеру телефона
     */
    private BotResponse handleFindContactByPhoneNumber(Long chatId) {
        BotResponse response = new BotResponse();
        String number = (String) stateService.getParamByKey(chatId, CONTACT_NUMBER);
        List<Contact> contacts = contactService.findContactsByNumber(chatId, number);

        if (contacts.isEmpty()) {
            stateService.changeCurrentOperation(chatId, Operation.CONTACTS_MENU);
            response.setText("По номеру " + number + " контакты не найдены.");
            response.setKeyboardText(ReplyKeyboardConstants.CONTACTS_MENU);
        } else {
            response.setText("По номеру " + number + " контакты успешно найдены.");
            List<String> names = contacts.stream()
                    .map(Contact::getName)
                    .toList();
            response.setInlineKeyboardText(new InlineKeyboardText(
                    names, Operation.CURRENT_CONTACT_MENU.toString()));
        }
        return response;
    }
}
