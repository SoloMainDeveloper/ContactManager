package org.example.operations.contact;

import org.example.constants.ReplyConstants;
import org.example.operations.OperationHandler;
import org.example.constants.UserCommandConstants;
import org.example.response.BotResponse;
import org.example.entity.Contact;
import org.example.constants.ReplyKeyboardConstants;
import org.example.service.ContactService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Обработчик события: Блокирование контакта
 */
@Component
public class BlockContactHandler implements OperationHandler {
    @Override
    public Operation getSupportedOperation() {
        return Operation.BLOCK_CONTACT;
    }

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
    public BlockContactHandler(ContactService contactService, StateService stateService) {
        this.contactService = contactService;
        this.stateService = stateService;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        switch (messageText) {
            case UserCommandConstants.YES -> {
                String contactName = (String) stateService.getParamByKey(
                        chatId,
                        "currentContactName");
                Optional<Contact> contactOptional = contactService
                        .findContactByName(chatId, contactName);
                if (contactOptional.isPresent()) {
                    Contact contact = contactOptional.get();
                    contactService.toggleContactBlocked(contact);

                    String blockActionInfo = contact.isBlocked()
                            ? "заблокирован"
                            : "разблокирован";
                    String responseText = String.format("Контакт " + contactName
                            + " успешно %s", blockActionInfo);

                    response.setText(responseText);
                } else {
                    response.setText("Контакта с именем " + contactName
                            + " не существует. Блокировка не применена");
                }
                response.setKeyboardText(ReplyKeyboardConstants.CONTACTS_MENU);
                stateService.changeCurrentOperation(chatId, Operation.CONTACTS_MENU);
            }
            case UserCommandConstants.NO -> {
                stateService.changeCurrentOperation(chatId, Operation.CONTACTS_MENU);
                response.setText("Действие изменения блокировки отменено");
                response.setKeyboardText(ReplyKeyboardConstants.CONTACTS_MENU);
            }
            default -> response.setText(ReplyConstants.UNKNOWN_COMMAND);
        }
        return response;
    }
}
