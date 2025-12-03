package org.example.operations.data;

import org.example.entity.AppDocument;
import org.example.entity.Contact;
import org.example.exceptions.ContactAlreadyExistsException;
import org.example.exceptions.ImportException;
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
        List<Contact> contacts;
        try {
            String fileName = stateService.getParamByKey(chatId, "fileName");
            contacts = importService.importContacts(
                    new AppDocument(fileName, messageText));
        } catch (ImportException e) {
            e.printStackTrace();
            response.setText("Произошла ошибка при импорте: " + e.getMessage());
            response.setKeyboardText(keyboardCreator.dataMenu());
            stateService.changeCurrentOperation(chatId, Operation.DATA_MENU, true);
            return response;
        }

        StringBuilder responseText = new StringBuilder();
        Integer counter = 0;
        for(Contact contact : contacts) {
            try {
                contactService.tryAddContact(chatId, contact);
                counter++;
            } catch (ContactAlreadyExistsException e) {
                responseText.append(e.getMessage()).append("\n");
            }
        }
        responseText.append("Импорт контактов закончен, добавилось %d из %d"
                .formatted(counter, contacts.size()));

        response.setText(responseText.toString());
        response.setKeyboardText(keyboardCreator.dataMenu());
        stateService.changeCurrentOperation(chatId, Operation.DATA_MENU, true);

        return response;
    }
}
