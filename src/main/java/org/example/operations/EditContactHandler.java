package org.example.operations;

import org.example.response.BotResponse;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
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
    private final ContactService service;

    /**
     * Конструктор
     */
    public EditContactHandler(ContactService service) {
        this.service = service;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.EDIT_CONTACT;
    }

    @Override
    public BotResponse handleMessage(State state, String messageText, Long chatId) {
        BotResponse response = new BotResponse();
        String contactName = state.getParamByKey("currentContactName");

        switch(messageText) {
            case "Имя" -> {
                response.setText("Введите имя контакта");
                state.setLastRequestedParamKey("contactName");
            }
            case "Номер" -> {
                response.setText("Введите номер телефона");
                state.setLastRequestedParamKey("contactNumber");
            }
            case "Возраст" -> {
                response.setText("Введите возраст");
                state.setLastRequestedParamKey("contactAge");
            }
            case "Пол" -> {
                response.setText("Выберите пол");
                response.setReplyMarkup(List.of("Мужской", "Женский"));
                state.setLastRequestedParamKey("contactGender");
            }
            case "Изменить контакт" -> {
                Boolean isSuccessful = service.tryUpdateContact(
                        chatId, contactName, state.getParams());
                if(isSuccessful) {
                    response.setText("Контакт " + contactName + " успешно изменен");
                } else {
                    response.setText("Контакт " + contactName
                            + " не был изменен, так как не был найден");
                }
                response.setReplyMarkup(keyboardCreator.contactsMenu());
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
            }
            case "Назад" -> {
                response.setText("Вы вернулись назад");
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                response.setReplyMarkup(keyboardCreator.contactsMenu());
            }
            default -> {
                return handleMessageWithContext(state, messageText);
            }
        }
        return response;
    }

    /**
     * Обрабатывает сообщение от пользователя. Заполняет контекст входными
     * данными, которые были запрошены ботом
     */
    private BotResponse handleMessageWithContext(State state, String messageText) {
        BotResponse response = new BotResponse();
        String lastRequestedParamKey = state.getLastRequestedParamKey();
        if (lastRequestedParamKey == null) {
            response.setText("Я не понимаю эту команду.");
            return response;
        }
        state.addParameter(lastRequestedParamKey, messageText);
        response.setText("Отлично. Выберите какие данные хотите изменить у контакта");
        response.setReplyMarkup(keyboardCreator.editContactMenu());
        return response;
    }
}
