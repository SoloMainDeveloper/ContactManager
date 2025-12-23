package org.example.operations.data;

import org.example.constants.ReplyConstants;
import org.example.constants.ReplyKeyboardConstants;
import org.example.response.AppDocument;
import org.example.entity.Contact;
import org.example.exceptions.ExportException;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.ContactService;
import org.example.service.export.ExportService;
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

    private static final String EXPORT_FORMAT = "exportFormat";

    private static final String EXPORT_FILE_NAME = "exportFileName";

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
            response.setText(ReplyConstants.UNKNOWN_COMMAND);
            return response;
        }
        switch (lastRequestedParamKey) {
            case EXPORT_FORMAT -> {
                stateService.addParameter(chatId, lastRequestedParamKey, messageText);
                response.setText("Введите имя экспортируемому файлу");
                stateService.setLastRequestedParamKey(chatId, EXPORT_FILE_NAME);
            }
            case EXPORT_FILE_NAME -> {
                List<Contact> contacts = contactService.findContactsByChatId(
                    chatId, new ContactFilter(), new ContactOrder());
                if (contacts.isEmpty()) {
                    response.setText("Вы еще не создали ни одного контакта");
                    response.setKeyboardText(ReplyKeyboardConstants.DATA_MENU);
                    stateService.changeCurrentOperation(chatId, Operation.DATA_MENU);
                    return response;
                }

                try {
                    String format = (String) stateService
                        .getParamByKey(chatId, EXPORT_FORMAT);
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
            default -> response.setText(ReplyConstants.UNKNOWN_COMMAND);
        }
        return response;
    }
}
