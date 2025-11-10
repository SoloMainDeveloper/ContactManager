package org.example.operations;

import org.example.response.BotResponse;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.springframework.stereotype.Component;

/**
 * Обработчик события: Удаление контакта
 */
@Component
public class DeleteContactHandler implements OperationHandler {
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
    public DeleteContactHandler(ContactService service) {
        this.service = service;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.DELETE_CONTACT;
    }

    @Override
    public BotResponse handleMessage(State state, String messageText, Long chatId) {
        BotResponse response = new BotResponse();
        switch(messageText) {
            case "Да":
                String contactName = state.getParamByKey("currentContactName");
                service.deleteByName(chatId, contactName);
                response.setText("Контакт " + contactName + " успешно удален");
                response.setReplyMarkup(keyboardCreator.contactsMenu());
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                break;
            case "Нет":
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                response.setText("Действие удаления текущего контакта отменено");
                response.setReplyMarkup(keyboardCreator.contactsMenu());
                break;
            default:
                response.setText("Я не понимаю эту команду.");
                break;
        }
        return response;
    }
}
