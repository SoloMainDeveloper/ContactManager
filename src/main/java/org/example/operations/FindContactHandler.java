package org.example.operations;

import org.example.response.BotResponse;
import org.example.response.InlineKeyboardText;
import org.example.keyboardcreator.InlineKeyboardCreator;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.entity.Contact;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Обработчик события: Поиск контакта
 */
@Component
public class FindContactHandler implements OperationHandler {
    /**
     * Создает меню из кнопок для быстрого ввода команд
     */
    private final ReplyKeyboardConstants replyKeyboardCreator = new ReplyKeyboardConstants();

    /**
     * Создает кнопки в ответе текста сообщения
     */
    private final InlineKeyboardCreator inlineKeyboardCreator = new InlineKeyboardCreator();

    /**
     * Сервис контактов
     */
    private final ContactService service;

    /**
     * Конструктор
     */
    public FindContactHandler(ContactService service) {
        this.service = service;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.FIND_CONTACT;
    }

    @Override
    public BotResponse handleMessage(State state, String messageText, Long chatId) {
        BotResponse response = new BotResponse();
        switch(messageText){
            case "Поиск по имени" -> {
                response.setText("Введите имя");
                state.setLastRequestedParamKey("contactName");
            }
            case "Поиск по номеру" -> {
                response.setText("Введите номер");
                state.setLastRequestedParamKey("contactNumber");
            }
            case "Назад" -> {
                response.setText("Вы вернулись назад");
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                response.setReplyMarkup(replyKeyboardCreator.contactsMenu());
            }
            default -> {
                return handleMessageWithContext(state, messageText, chatId);
            }
        }
        return response;
    }

    /**
     * Обрабатывает сообщение от пользователя. Заполняет контекст входными данными, которые были запрошены ботом, и затем
     * использует их при поиске.
     */
    private BotResponse handleMessageWithContext(State state, String messageText,
                                            Long chatId) {
        String lastRequestedParamKey = state.getLastRequestedParamKey();
        if (lastRequestedParamKey == null) {
            return new BotResponse("Я не понимаю эту команду.");
        }
        state.addParameter(lastRequestedParamKey, messageText);

        return switch (lastRequestedParamKey) {
            case "contactName" -> handleFindContactByName(state, chatId);
            case "contactNumber" -> handleFindContactByPhoneNumber(state, chatId);
            default -> new BotResponse("Я не понимаю эту команду.");
        };
    }

    /**
     * Обрабатывает команду нахождения контакта по имени
     */
    private BotResponse handleFindContactByName(State state, Long chatId) {
        BotResponse response = new BotResponse();
        String name = state.getParamByKey("contactName");
        Optional<Contact> contact = service.findContactByName(chatId, name);

        if(contact.isPresent()) {
            response.setText("По имени " + name + " контакт успешно найден.");
            response.setInlineKeyboardText(new InlineKeyboardText(
                    List.of(contact.get().getName()),
                    Operation.CURRENT_CONTACT_MENU.toString()));
        } else {
            state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
            response.setText("По имени " + name + " контакты не найдены.");
            response.setReplyMarkup(replyKeyboardCreator.contactsMenu());
        }
        return response;
    }

    /**
     * Обрабатывает команду нахождения контакта номеру телефона
     */
    private BotResponse handleFindContactByPhoneNumber(State state, Long chatId) {
        BotResponse response = new BotResponse();
        String number = state.getParamByKey("contactNumber");
        Optional<Contact> contact = service.findContactByNumber(chatId, number);

        if(contact.isPresent()) {
            response.setText("По номеру " + number + " контакт успешно найден.");
            response.setInlineKeyboardText(new InlineKeyboardText(
                    List.of(contact.get().getName()),
                    Operation.CURRENT_CONTACT_MENU.toString()));
        } else {
            state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
            response.setText("По номеру " + number + " контакты не найдены.");
            response.setReplyMarkup(replyKeyboardCreator.contactsMenu());
        }
        return response;
    }
}
