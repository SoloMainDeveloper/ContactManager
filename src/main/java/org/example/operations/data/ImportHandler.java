package org.example.operations.data;

import org.example.entity.Contact;
import org.example.exceptions.UnsupportedFormatException;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.ContactService;
import org.example.service.ImportService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Обработчик события: импорт контактов
 */
@Component
public class ImportHandler implements OperationHandler {
    /**
     * Создает меню из кнопок для быстрого ввода команд
     */
    private final ReplyKeyboardConstants keyboardCreator = new ReplyKeyboardConstants();

    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Сервис импорта контактов
     */
    private final ImportService importService;

    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    /**
     * Конструктор
     */
    @Autowired
    public ImportHandler(StateService stateService, ImportService importService,
                         ContactService contactService){
        this.stateService = stateService;
        this.importService = importService;
        this.contactService = contactService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.IMPORT_CONTACTS;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        if(Objects.equals(messageText, "Назад")) {
            response.setText("Вы вернулись назад");
            stateService.changeCurrentOperation(chatId, Operation.DATA_MENU, true);
            response.setKeyboardText(keyboardCreator.dataMenu());
            return response;
        }
        try {
            String fileName = stateService.getParamByKey(chatId, "fileName");
            List<Contact> contacts = importService.importContacts(fileName, messageText);
            for(Contact contact : contacts) {
                contactService.tryAddContact(chatId, contact);
            }
        } catch (UnsupportedFormatException e) {
            response.setText(e.getMessage());
        }
        return response;
    }
}
