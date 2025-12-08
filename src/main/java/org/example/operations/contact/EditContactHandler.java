package org.example.operations.contact;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.exceptions.ContactDoesNotExistException;
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
import java.util.Optional;

/**
 * Обработчик события: Редактирование контакта
 */
@Component
public class EditContactHandler implements OperationHandler {
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
    public EditContactHandler(ContactService contactService, StateService stateService) {
        this.contactService = contactService;
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.EDIT_CONTACT;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        String contactName = (String) stateService.getParamByKey(
                chatId, "currentContactName");

        switch (messageText) {
            case "Имя" -> {
                response.setText("Введите имя контакта");
                stateService.setLastRequestedParamKey(chatId, "newContactName");
            }
            case "Номер" -> {
                response.setText("Введите номер телефона");
                stateService.setLastRequestedParamKey(chatId, "contactNumber");
            }
            case "Возраст" -> {
                response.setText("Введите возраст");
                stateService.setLastRequestedParamKey(chatId, "contactAge");
            }
            case "Пол" -> {
                response.setText("Выберите пол");
                response.setKeyboardText(List.of("Мужской", "Женский"));
                stateService.setLastRequestedParamKey(chatId, "contactGender");
            }
            case "Изменить контакт" -> {
                try {
                    Contact contact = contactService.findContactByName(
                            chatId, contactName).orElseThrow(() ->
                            new ContactDoesNotExistException(
                                    "Контакт %s не был найден".formatted(contactName)));
                    Map<String, Object> params = stateService.getParams(chatId);

                    contact.setName((String) params.getOrDefault(
                            "newContactName", contact.getName()));
                    contact.setPhoneNumber((String) params.getOrDefault(
                            "contactNumber", contact.getPhoneNumber()));
                    contact.setAge(Integer.parseInt((String) params.getOrDefault(
                            "contactAge", contact.getAge())));
                    contact.setGender(Gender.fromDisplayName((String) params.getOrDefault(
                            "contactGender", contact.getGender())));
                    contactService.tryUpdateContact(chatId, contactName, contact);
                    response.setText("Контакт " + contactName + " успешно изменен");
                } catch (ContactDoesNotExistException e) {
                    e.printStackTrace();
                    response.setText("Произошла ошибка при изменении: " + e.getMessage());
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
     * Обрабатывает сообщение от пользователя. Заполняет контекст входными
     * данными, которые были запрошены ботом
     */
    private BotResponse handleMessageWithContext(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        String lastRequestedParamKey = stateService.getLastRequestedParamKey(chatId);
        if (lastRequestedParamKey == null) {
            response.setText(ReplyConstants.UNKNOWN_COMMAND);
            return response;
        }
        stateService.addParameter(chatId, lastRequestedParamKey, messageText);
        response.setText("Отлично. Выберите какие данные хотите изменить у контакта");
        response.setKeyboardText(ReplyKeyboardConstants.EDIT_CONTACT_MENU);
        return response;
    }
}
