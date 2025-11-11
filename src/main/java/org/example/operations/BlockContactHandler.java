package org.example.operations;

import org.example.response.BotResponse;
import org.example.entity.Contact;
import org.example.keyboardcreator.ReplyKeyboardConstants;
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
    /**
     * Создает меню из кнопок для быстрого ввода команд
     */
    private final ReplyKeyboardConstants keyboardCreator = new ReplyKeyboardConstants();

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
        switch(messageText) {
            case "Да" -> {
                String contactName = stateService.getParamByKey(
                        chatId,
                        "currentContactName");
                Optional<Contact> contactOptional = contactService
                        .findContactByName(chatId, contactName);
                if (contactOptional.isPresent()) {
                    Contact contact = contactOptional.get();
                    contact.setBlocked(!contact.isBlocked());
                    contactService.updateBlockField(contact);

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
                response.setKeyboardText(keyboardCreator.contactsMenu());
                stateService.changeCurrentOperation(chatId, Operation.CONTACTS_MENU,
                        true);
            }
            case "Нет" -> {
                stateService.changeCurrentOperation(chatId, Operation.CONTACTS_MENU,
                        true);
                response.setText("Действие изменения блокировки отменено");
                response.setKeyboardText(keyboardCreator.contactsMenu());
            }
            default -> response.setText("Я не понимаю эту команду.");
        }
        return response;
    }
}
