package org.example.operations.contact;

import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.service.ContactService;
import org.example.service.StateService;
import org.example.state.Operation;
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
    private final ContactService contactService;

    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Конструктор
     */
    public DeleteContactHandler(ContactService contactService, StateService stateService) {
        this.contactService = contactService;
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.DELETE_CONTACT;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        switch(messageText) {
            case "Да":
                String contactName = stateService.getParamByKey(chatId,
                        "currentContactName");
                contactService.deleteByName(chatId, contactName);
                response.setText("Контакт " + contactName + " успешно удален");
                response.setKeyboardText(keyboardCreator.contactsMenu());
                stateService.changeCurrentOperation(
                        chatId, Operation.CONTACTS_MENU, true);
                break;
            case "Нет":
                stateService.changeCurrentOperation(
                        chatId, Operation.CONTACTS_MENU, true);
                response.setText("Действие удаления текущего контакта отменено");
                response.setKeyboardText(keyboardCreator.contactsMenu());
                break;
            default:
                response.setText("Я не понимаю эту команду.");
                break;
        }
        return response;
    }
}
