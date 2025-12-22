package org.example.operations.contact;

import org.example.constants.ReplyConstants;
import org.example.operations.OperationHandler;
import org.example.constants.UserCommandConstants;
import org.example.response.BotResponse;
import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.constants.ReplyKeyboardConstants;
import org.example.service.ContactService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Обработчик события: действия пользователя в меню текущего контакта
 */
@Component
public class CurrentContactMenuHandler implements OperationHandler {
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
    public CurrentContactMenuHandler(ContactService contactService, StateService stateService) {
        this.contactService = contactService;
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.CURRENT_CONTACT_MENU;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        String contactName = (String) stateService.getParamByKey(chatId, "currentContactName");
        Optional<Contact> contactOptional = contactService.findContactByName(
                chatId, contactName);
        if (contactOptional.isEmpty()) {
            response.setText("Контакт " + contactName + " не был найден");
            response.setKeyboardText(ReplyKeyboardConstants.CONTACTS_MENU);
            stateService.changeCurrentOperation(chatId, Operation.CONTACTS_MENU);
            return response;
        }
        Contact contact = contactOptional.get();
        switch (messageText) {
            case UserCommandConstants.CONTACT_MENU -> {
                response.setText("Меню для контакта " + contactName + " вызвано");
                response.setKeyboardText(ReplyKeyboardConstants.CURRENT_CONTACT_MENU);
            }
            case UserCommandConstants.INFO -> {
                response.setText(getContactInfo(contact));
                response.setKeyboardText(ReplyKeyboardConstants.CONTACTS_MENU);
                stateService.changeCurrentOperation(chatId, Operation.CONTACTS_MENU);
            }
            case UserCommandConstants.EDIT -> {
                stateService.changeCurrentOperation(chatId, Operation.EDIT_CONTACT);
                response.setText("Отлично. Выберите какие данные хотите изменить у контакта");
                response.setKeyboardText(ReplyKeyboardConstants.EDIT_CONTACT_MENU);
            }
            case UserCommandConstants.BLOCK -> {
                stateService.changeCurrentOperation(chatId, Operation.BLOCK_CONTACT);

                String isBlockedInfo = contact.isBlocked()
                        ? "заблокирован"
                        : "не заблокирован";
                String blockActionInfo = !contact.isBlocked()
                        ? "заблокировать"
                        : "разблокировать";
                String responseText = String.format("Текущий контакт %s. Вы хотите %s?",
                        isBlockedInfo, blockActionInfo);

                response.setText(responseText);
                response.setKeyboardText(ReplyKeyboardConstants.YES_NO);
            }
            case UserCommandConstants.DELETE -> {
                stateService.changeCurrentOperation(chatId, Operation.DELETE_CONTACT);
                response.setText("Вы точно хотите удалить текущий контакт?");
                response.setKeyboardText(ReplyKeyboardConstants.YES_NO);
            }
            case UserCommandConstants.BACK -> {
                response.setText(ReplyConstants.COME_BACK);
                stateService.changeCurrentOperation(chatId, Operation.CONTACTS_MENU);
                response.setKeyboardText(ReplyKeyboardConstants.CONTACTS_MENU);
            }
            default -> response.setText(ReplyConstants.UNKNOWN_COMMAND);
        }
        return response;
    }

    /**
     * Возвращает информацию о контакте
     */
    private String getContactInfo(Contact contact) {
        String notDetermined = "не указан";
        String phoneNumberInfo = contact.getPhoneNumber().isEmpty()
                ? notDetermined
                : contact.getPhoneNumber();
        String genderInfo = "";
        switch (contact.getGender()) {
            case Gender.MALE -> genderInfo = "мужской";
            case Gender.FEMALE -> genderInfo = "женский";
            case Gender.NOT_SPECIFIED -> genderInfo = notDetermined;
        }
        String ageInfo = contact.getAge() == -1
                ? notDetermined
                : String.valueOf(contact.getAge());
        String isBlockedInfo = contact.isBlocked()
                ? "Заблокирован"
                : "Не заблокирован";

        return String.format("Контакт: %s\nНомер: %s\nПол: %s\nВозраст: %s\n%s",
                contact.getName(), phoneNumberInfo, genderInfo, ageInfo, isBlockedInfo);
    }
}
