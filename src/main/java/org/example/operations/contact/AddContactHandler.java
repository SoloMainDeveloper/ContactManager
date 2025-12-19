package org.example.operations.contact;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.exceptions.ContactAlreadyExistsException;
import org.example.keyboardcreator.ReplyConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.service.ContactService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Обработчик события: Добавление контакта
 */
@Component
public class AddContactHandler implements OperationHandler {
    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Название параметра запроса возраста контакта
     */
    private static final String CONTACT_AGE = "contactAge";

    /**
     * Название параметра запроса номера контакта
     */
    private static final String CONTACT_NUMBER = "contactNumber";

    /**
     * Название параметра запроса пола контакта
     */
    private static final String CONTACT_GENDER = "contactGender";

    /**
     * Конструктор
     */
    public AddContactHandler(ContactService contactService, StateService stateService) {
        this.contactService = contactService;
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.ADD_CONTACT;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        switch (messageText) {
            case "Номер" -> {
                response.setText("Введите номер телефона");
                stateService.setLastRequestedParamKey(chatId, CONTACT_NUMBER);
            }
            case "Возраст" -> {
                response.setText("Введите возраст");
                stateService.setLastRequestedParamKey(chatId, CONTACT_AGE);
            }
            case "Пол" -> {
                response.setText("Выберите пол");
                response.setKeyboardText(ReplyKeyboardConstants.GENDERS);
                stateService.setLastRequestedParamKey(chatId, CONTACT_GENDER);
            }
            case "Сохранить контакт" -> {
                try {
                    Map<String, Object> params = stateService.getParams(chatId);
                    Contact contact = new Contact(chatId,
                            (String) params.get("contactName"),
                            (String) params.getOrDefault(CONTACT_NUMBER, ""),
                            Integer.parseInt((String) params.getOrDefault(CONTACT_AGE,
                                    String.valueOf(-1))),
                            Gender.fromDisplayName((String) params.get(CONTACT_GENDER)),
                            false
                    );
                    contactService.tryAddContact(chatId, contact);
                    response.setText(
                            "Контакт " + contact.getName() + " успешно добавлен");
                } catch (ContactAlreadyExistsException e) {
                    e.printStackTrace();
                    response.setText("Произошла ошибка при добавлении: " + e.getMessage());
                }
                response.setKeyboardText(ReplyKeyboardConstants.CONTACTS_MENU);
                stateService.changeCurrentOperation(chatId, Operation.CONTACTS_MENU);
            }
            case "Назад" -> {
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
     * Обрабатывает сообщение от пользователя. Заполняет контекст входными данными, которые были запрошены ботом
     */
    private BotResponse handleMessageWithContext(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        String lastRequestedParamKey = stateService.getLastRequestedParamKey(chatId);
        if (lastRequestedParamKey == null) {
            response.setText(ReplyConstants.UNKNOWN_COMMAND);
            return response;
        }
        stateService.addParameter(chatId, lastRequestedParamKey, messageText);
        response.setText("Отлично. Выберите какие данные хотите задать контакту");
        response.setKeyboardText(ReplyKeyboardConstants.ADD_CONTACT_MENU);
        return response;
    }
}
