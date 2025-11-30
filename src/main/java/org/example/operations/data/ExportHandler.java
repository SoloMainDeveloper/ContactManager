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
import org.example.utils.GroupConverter;
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
                         ContactService contactService){
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
        switch (messageText) {
            case "Добавить контакт" -> {
                stateService.setLastRequestedParamKey(chatId, "exportContactName");
                response.setText("Введите имя контакта для добавления в экспорт");
            }
            case "Экспортировать" -> {
                String fileName = stateService.getParamByKey(chatId, "exportFileName");
                String format = stateService.getParamByKey(chatId, "exportFormat");
                Set<Long> contactIds = new GroupConverter().stringToContactIds(
                        stateService.getParamByKey(chatId, "exportContactIds"));
                List<Contact> contacts = contactIds.stream()
                        .map(id -> contactService.findContactById(chatId, id))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList();
                try {
                    AppDocument document = exportService.exportContacts(
                            fileName, format, contacts);
                    response.setDocument(document);
                    response.setText("Контакты были успешно экспортированы в файл");
                } catch (UnsupportedFormatException e) {
                    response.setText(e.getMessage());
                }
            }
            case "Назад" -> {
                response.setText("Вы вернулись назад");
                stateService.changeCurrentOperation(chatId, Operation.DATA_MENU, true);
                response.setKeyboardText(keyboardCreator.dataMenu());
            }
            default -> {
                return handleMessageWithContext(chatId, messageText);
            }
        }
        return response;
    }

    /**
     * Обрабатывает сообщение от пользователя. Заполняет контекст входными данными,
     * которые были запрошены ботом
     */
    private BotResponse handleMessageWithContext(Long chatId, String messageText) {
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
                stateService.addParameter(chatId, lastRequestedParamKey, messageText);
                response.setText("Добавьте контакты, которые хотите экспортировать");
                response.setKeyboardText(keyboardCreator.exportDetailsMenu());
            }
            case "exportContactName" -> {
                Optional<Contact> contact = contactService.findContactByName(
                        chatId, messageText);
                if(contact.isPresent()) {
                    GroupConverter converter = new GroupConverter();
                    Set<Long> contactIds = converter.stringToContactIds(
                            stateService.getParamByKey(chatId, "exportContactIds"));
                    Long contactId = contact.get().getId();
                    if(contactIds.contains(contactId)) {
                        response.setText("Контакт с таким именем уже добавлен");
                    } else {
                        contactIds.add(contactId);
                        stateService.addParameter(chatId, "exportContactIds",
                                converter.contactIdsToString(contactIds));
                        response.setText("Контакт " + messageText + " успешно добавлен в "
                                + "группу. Желаете добавить ещё контактов к экспорту?");
                        response.setKeyboardText(keyboardCreator.exportDetailsMenu());
                    }
                } else {
                    response.setText("Контакт с именем '" + messageText + "' не найден");
                    response.setKeyboardText(keyboardCreator.exportDetailsMenu());
                }
            }
            default -> response.setText("Я не понимаю эту команду.");
        }
        return response;
    }
}
