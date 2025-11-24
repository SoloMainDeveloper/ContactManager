package org.example.operations;

import org.example.exceptions.ContactAlreadyExistsException;
import org.example.response.BotResponse;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.service.ContactService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Обработчик события: Добавление контакта
 */
@Component
public class AddContactHandler implements OperationHandler {
    /**
     * Создает меню из кнопок для быстрого ввода команд
     */
    private final ReplyKeyboardConstants keyboardCreator = new ReplyKeyboardConstants();

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
        switch(messageText){
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
            case "Сохранить контакт" -> {
                String contactName = stateService.getParamByKey(chatId, "contactName");
                try {
                    contactService.tryAddContact(chatId, stateService.getParams(chatId));
                    response.setText("Контакт " + contactName + " успешно добавлен");
                } catch (ContactAlreadyExistsException e) {
                    e.printStackTrace();
                    response.setText("Произошла ошибка при добавлении: " + e.getMessage());
                }
                response.setKeyboardText(keyboardCreator.contactsMenu());
                stateService.changeCurrentOperation(chatId, Operation.CONTACTS_MENU, true);
            }
            case "Назад" -> {
                response.setText("Вы вернулись назад");
                stateService.changeCurrentOperation(chatId, Operation.CONTACTS_MENU, true);
                response.setKeyboardText(keyboardCreator.contactsMenu());
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
            response.setText("Я не понимаю эту команду.");
            return response;
        }
        stateService.addParameter(chatId, lastRequestedParamKey, messageText);
        response.setText("Отлично. Выберите какие данные хотите задать контакту");
        response.setKeyboardText(keyboardCreator.addContactMenu());
        return response;
    }
}
