package org.example.operations.contact;

import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.service.ContactService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Обработчик события: Редактирование контакта
 */
@Component
public class EditContactHandler implements OperationHandler {
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
        String contactName = stateService.getParamByKey(chatId, "currentContactName");

        switch(messageText) {
            case "Имя" -> {
                response.setText("Введите имя контакта");
                stateService.setLastRequestedParamKey(chatId, "contactName");
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
                Boolean isSuccessful = contactService.tryUpdateContact(
                        chatId, contactName, stateService.getParams(chatId));
                if(isSuccessful) {
                    response.setText("Контакт " + contactName + " успешно изменен");
                } else {
                    response.setText("Контакт " + contactName
                            + " не был изменен, так как не был найден");
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
     * Обрабатывает сообщение от пользователя. Заполняет контекст входными
     * данными, которые были запрошены ботом
     */
    private BotResponse handleMessageWithContext(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        String lastRequestedParamKey = stateService.getLastRequestedParamKey(chatId);
        if (lastRequestedParamKey == null) {
            response.setText("Я не понимаю эту команду.");
            return response;
        }
        stateService.addParameter(chatId, lastRequestedParamKey, messageText);
        response.setText("Отлично. Выберите какие данные хотите изменить у контакта");
        response.setKeyboardText(keyboardCreator.editContactMenu());
        return response;
    }
}
