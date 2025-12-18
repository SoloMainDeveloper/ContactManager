package org.example.operations.data;

import org.example.entity.AppDocument;
import org.example.entity.Contact;
import org.example.exceptions.ExportException;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.ContactService;
import org.example.service.ExportService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.example.utils.ContactFilter;
import org.example.utils.ContactOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Обработчик события: экспорт контактов
 */
@Component
public class ExportHandler implements OperationHandler {
    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Сервис экспорта контактов
     */
    private final ExportService exportService;

    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    /**
     * Конструктор
     */
    @Autowired
    public ExportHandler(StateService stateService, ExportService exportService,
                         ContactService contactService) {
        this.stateService = stateService;
        this.exportService = exportService;
        this.contactService = contactService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.EXPORT_CONTACTS;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        String lastRequestedParamKey = stateService.getLastRequestedParamKey(chatId);
        if (lastRequestedParamKey == null) {
            response.setText("Я не понимаю эту команду.");
            return response;
        }
        switch (lastRequestedParamKey) {
            case "exportFormat" -> {
                stateService.addParameter(chatId, lastRequestedParamKey, messageText);
                response.setText("Введите имя экспортируемому файлу");
                stateService.setLastRequestedParamKey(chatId, "exportFileName");
            }
            case "exportFileName" -> {
                String format = (String) stateService.getParamByKey(chatId, "exportFormat");
                List<Contact> contacts = contactService.findContactsByChatId(
                    chatId, ContactFilter.none(), ContactOrder.none());
                try {
                    AppDocument document = exportService
                            .exportContacts(messageText, format, contacts);
                    response.setDocument(document);
                    response.setText("Контакты были успешно экспортированы в файл");
                } catch (ExportException e) {
                    e.printStackTrace();
                    response.setText("Произошла ошибка при экспорте: " + e.getMessage());
                }

                response.setKeyboardText(ReplyKeyboardConstants.DATA_MENU);
                stateService.changeCurrentOperation(chatId, Operation.DATA_MENU);
            }
            default -> response.setText("Я не понимаю эту команду.");
        }
        return response;
    }
}
