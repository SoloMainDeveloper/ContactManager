package org.example.operations;

import org.example.response.BotResponse;
import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Обработчик события: действия пользователя в меню текущего контакта
 */
@Component
public class CurrentContactMenuHandler implements OperationHandler {
    /**
     * Создает меню из кнопок для быстрого ввода команд
     */
    private final ReplyKeyboardConstants keyboardCreator = new ReplyKeyboardConstants();

    /**
     * Сервис контактов
     */
    private final ContactService service;

    /**
     * Конструктор
     */
    public CurrentContactMenuHandler(ContactService service) {
        this.service = service;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.CURRENT_CONTACT_MENU;
    }

    @Override
    public BotResponse handleMessage(State state, String messageText, Long chatId) {
        BotResponse response = new BotResponse();
        String contactName = state.getParamByKey("currentContactName");
        Contact contact = service.findContactByName(chatId, contactName).orElse(null);
        if(contact == null){
            response.setText("Контакт " + contactName + " не был найден");
            return response;
        }
        switch (messageText) {
            case "Меню пользователя вызвано" -> {
                response.setText("Меню для пользователя " + contactName + " вызвано");
                response.setReplyMarkup(keyboardCreator.currentContactMenu());
            }
            case "Информация" -> {
                response.setText(getContactInfo(contact));
                response.setReplyMarkup(keyboardCreator.contactsMenu());
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
            }
            case "Изменить" -> {
                state.changeCurrentOperation(Operation.EDIT_CONTACT, false);
                response.setText("Отлично. Выберите какие данные хотите изменить у контакта");
                response.setReplyMarkup(keyboardCreator.editContactMenu());
            }
            case "Блокировать" -> {
                state.changeCurrentOperation(Operation.BLOCK_CONTACT, false);

                String isBlockedInfo = contact.isBlocked()
                        ? "заблокирован"
                        : "не заблокирован";
                String blockActionInfo = !contact.isBlocked()
                        ? "заблокировать"
                        : "разблокировать";
                String responseText = String.format("Текущий контакт %s. Вы хотите %s?",
                        isBlockedInfo, blockActionInfo);

                response.setText(responseText);
                response.setReplyMarkup(List.of("Да", "Нет"));
            }
            case "Удалить" -> {
                state.changeCurrentOperation(Operation.DELETE_CONTACT, false);
                response.setText("Вы точно хотите удалить текущий контакт?");
                response.setReplyMarkup(List.of("Да", "Нет"));
            }
            case "Назад" -> {
                response.setText("Вы вернулись назад");
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                response.setReplyMarkup(keyboardCreator.contactsMenu());
            }
            default -> response.setText("Я не понимаю эту команду");
        }
        return response;
    }

    /**
     * Возвращает информацию о контакте
     */
    private String getContactInfo(Contact contact) {
        String phoneNumberInfo = contact.getPhoneNumber().isEmpty()
                ? "не указан"
                : contact.getPhoneNumber();
        String genderInfo = "";
        switch (contact.getGender()){
            case Gender.MALE -> genderInfo = "мужской";
            case Gender.FEMALE -> genderInfo = "женский";
            case Gender.NOT_SPECIFIED -> genderInfo = "не указан";
        }
        String ageInfo = contact.getAge() == -1
                ? "не указан"
                : String.valueOf(contact.getAge());
        String isBlockedInfo = contact.isBlocked()
                ? "Заблокирован"
                : "Не заблокирован";

        return String.format("Контакт: %s\nНомер: %s\nПол: %s\nВозраст: %s\n%s",
                contact.getName(), phoneNumberInfo, genderInfo, ageInfo, isBlockedInfo);
    }
}
