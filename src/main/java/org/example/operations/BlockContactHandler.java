package org.example.operations;

import org.example.response.BotResponse;
import org.example.entity.Contact;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
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
    private final ContactService service;

    /**
     * Конструктор
     */
    public BlockContactHandler(ContactService service) {
        this.service = service;
    }

    @Override
    public BotResponse handleMessage(State state, String messageText, Long chatId) {
        BotResponse response = new BotResponse();
        switch(messageText) {
            case "Да" -> {
                String contactName = state.getParamByKey("currentContactName");
                Optional<Contact> contactOptional = service
                        .findContactByName(chatId, contactName);
                if (contactOptional.isPresent()) {
                    Contact contact = contactOptional.get();
                    contact.setBlocked(!contact.isBlocked());
                    service.updateBlockField(contact);

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
                response.setReplyMarkup(keyboardCreator.contactsMenu());
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
            }
            case "Нет" -> {
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                response.setText("Действие изменения блокировки отменено");
                response.setReplyMarkup(keyboardCreator.contactsMenu());
            }
            default -> response.setText("Я не понимаю эту команду.");
        }
        return response;
    }
}
