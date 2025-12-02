package org.example.operations.data;

import org.example.entity.AppDocument;
import org.example.entity.Contact;
import org.example.exceptions.UnsupportedFormatException;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.ContactService;
import org.example.service.ExportService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.example.utils.converter.GroupConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Обработчик события: экспорт контактов
 */
@Component
public class ExportHandler implements OperationHandler {
    /**
     * Создает меню из кнопок для быстрого ввода команд
     */
    private final ReplyKeyboardConstants keyboardCreator = new ReplyKeyboardConstants();

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
                String format = stateService.getParamByKey(chatId, "exportFormat");
                List<Contact> contacts = contactService.findContactsByChatId(chatId);
                try {
                    AppDocument document = exportService
                            .exportContacts(messageText, format, contacts);
                    response.setDocument(document);
                    response.setText("Контакты были успешно экспортированы в файл");
                } catch (UnsupportedFormatException e) {
                    response.setText(e.getMessage());
                }

                response.setKeyboardText(keyboardCreator.dataMenu());
                stateService.changeCurrentOperation(chatId, Operation.DATA_MENU, true);
            }
            default -> response.setText("Я не понимаю эту команду.");
        }
        return response;
    }
}
