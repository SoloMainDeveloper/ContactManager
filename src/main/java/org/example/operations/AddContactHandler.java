package org.example.operations;

import org.example.response.BotResponse;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
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
    private final ContactService service;

    /**
     * Конструктор
     */
    public AddContactHandler(ContactService service) {
        this.service = service;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.ADD_CONTACT;
    }

    @Override
    public BotResponse handleMessage(State state, String messageText, Long chatId) {
        BotResponse response = new BotResponse();
        switch(messageText){
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
            case "Сохранить контакт" -> {
                boolean isSuccessful = service.tryAddContact(chatId, state.getParams());
                String contactName = state.getParamByKey("contactName");
                if (isSuccessful) {
                    response.setText("Контакт " + contactName + " успешно добавлен");
                } else {
                    response.setText("Контакт " + contactName
                            + " не был добавлен. Произошла ошибка");
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
     * Обрабатывает сообщение от пользователя. Заполняет контекст входными данными, которые были запрошены ботом
     */
    private BotResponse handleMessageWithContext(State state, String messageText) {
        BotResponse response = new BotResponse();
        String lastRequestedParamKey = state.getLastRequestedParamKey();
        if (lastRequestedParamKey == null) {
            response.setText("Я не понимаю эту команду.");
            return response;
        }
        state.addParameter(lastRequestedParamKey, messageText);
        response.setText("Отлично. Выберите какие данные хотите задать контакту");
        response.setReplyMarkup(keyboardCreator.addContactMenu());
        return response;
    }
}
