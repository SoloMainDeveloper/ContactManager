package org.example.operations;

import org.example.response.BotResponse;
import org.example.response.InlineKeyboardText;
import org.example.keyboardcreator.InlineKeyboardCreator;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.entity.Contact;
import org.example.service.ContactService;
import org.example.service.StateService;
import org.example.state.Operation;
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
    private final ContactService contactService;

    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Конструктор
     */
    public FindContactHandler(ContactService contactService, StateService stateService) {
        this.contactService = contactService;
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.FIND_CONTACT;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        switch(messageText){
            case "Поиск по имени" -> {
                response.setText("Введите имя");
                stateService.setLastRequestedParamKey(chatId, "contactName");
            }
            case "Поиск по номеру" -> {
                response.setText("Введите номер");
                stateService.setLastRequestedParamKey(chatId, "contactNumber");
            }
            case "Назад" -> {
                response.setText("Вы вернулись назад");
                stateService.changeCurrentOperation(chatId, Operation.CONTACTS_MENU, true);
                response.setKeyboardText(replyKeyboardCreator.contactsMenu());
            }
            default -> {
                return handleMessageWithContext(chatId, messageText);
            }
        }
        return response;
    }

    /**
     * Обрабатывает сообщение от пользователя. Заполняет контекст входными данными, которые были запрошены ботом, и затем
     * использует их при поиске.
     */
    private BotResponse handleMessageWithContext(Long chatId, String messageText) {
        String lastRequestedParamKey = stateService.getLastRequestedParamKey(chatId);
        if (lastRequestedParamKey == null) {
            return new BotResponse("Я не понимаю эту команду.");
        }
        stateService.addParameter(chatId, lastRequestedParamKey, messageText);

        return switch (lastRequestedParamKey) {
            case "contactName" -> handleFindContactByName(chatId);
            case "contactNumber" -> handleFindContactByPhoneNumber(chatId);
            default -> new BotResponse("Я не понимаю эту команду.");
        };
    }

    /**
     * Обрабатывает команду нахождения контакта по имени
     */
    private BotResponse handleFindContactByName(Long chatId) {
        BotResponse response = new BotResponse();
        String name = stateService.getParamByKey(chatId, "contactName");
        Optional<Contact> contact = contactService.findContactByName(chatId, name);

        if(contact.isPresent()) {
            response.setText("По имени " + name + " контакт успешно найден.");
            response.setInlineKeyboardText(new InlineKeyboardText(
                    List.of(contact.get().getName()),
                    Operation.CURRENT_CONTACT_MENU.toString()));
        } else {
            stateService.changeCurrentOperation(chatId, Operation.CONTACTS_MENU, true);
            response.setText("По имени " + name + " контакты не найдены.");
            response.setKeyboardText(replyKeyboardCreator.contactsMenu());
        }
        return response;
    }

    /**
     * Обрабатывает команду нахождения контакта номеру телефона
     */
    private BotResponse handleFindContactByPhoneNumber(Long chatId) {
        BotResponse response = new BotResponse();
        String number = stateService.getParamByKey(chatId, "contactNumber");
        List<Contact> contacts = contactService.findContactByNumber(chatId, number);

        if(contacts.isEmpty()) {
            stateService.changeCurrentOperation(chatId, Operation.CONTACTS_MENU, true);
            response.setText("По номеру " + number + " контакты не найдены.");
            response.setKeyboardText(replyKeyboardCreator.contactsMenu());
        } else {
            response.setText("По номеру " + number + " контакт успешно найден.");
            List<String> names = contacts.stream()
                    .map(Contact::getName)
                    .toList();
            response.setInlineKeyboardText(new InlineKeyboardText(
                    names, Operation.CURRENT_CONTACT_MENU.toString()));
        }
        return response;
    }
}
