package org.example.operations;

import org.example.keyboardcreator.InlineKeyboardCreator;
import org.example.keyboardcreator.ReplyKeyboardCreator;
import org.example.entity.Contact;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

/**
 * Обработчик события: Поиск контакта
 */
public class FindContactHandler implements OperationHandler {
    /**
     * Создает меню из кнопок для быстрого ввода команд
     */
    private final ReplyKeyboardCreator replyKeyboardCreator = new ReplyKeyboardCreator();

    /**
     * Создает кнопки в ответе текста сообщения
     */
    private final InlineKeyboardCreator inlineKeyboardCreator = new InlineKeyboardCreator();

    @Override
    public SendMessage handleMessage(ContactService service, State state, String messageText, Long chatId) {
        SendMessage response = new SendMessage();
        switch(messageText){
            case "Поиск по имени":
                response.setText("Введите имя");
                state.setLastRequestedParamKey("contactName");
                break;
            case "Поиск по номеру":
                response.setText("Введите номер");
                state.setLastRequestedParamKey("contactNumber");
                break;
            case "Назад":
                response.setText("Вы вернулись назад");
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                response.setReplyMarkup(replyKeyboardCreator.contactsMenu());
                break;
            default:
                return handleMessageWithContext(service, state, messageText, chatId);
        }
        return response;
    }

    /**
     * Обрабатывает сообщение от пользователя. Заполняет контекст входными данными, которые были запрошены ботом, и затем
     * использует их при поиске.
     */
    private SendMessage handleMessageWithContext(ContactService service, State state, String messageText, Long chatId) {
        SendMessage response = new SendMessage();
        String lastRequestedParamKey = state.getLastRequestedParamKey();
        if (lastRequestedParamKey == null) {
            response.setText("Я не понимаю эту команду.");
            return response;
        }
        state.addParameter(lastRequestedParamKey, messageText);

        return switch (lastRequestedParamKey) {
            case "contactName" -> handleFindContactByName(service, state, chatId);
            case "contactNumber" -> handleFindContactByPhoneNumber(service, state, chatId);
            default -> response;
        };
    }

    /**
     * Обрабатывает команду нахождения контакта по имени
     */
    private SendMessage handleFindContactByName(ContactService service, State state, Long chatId) {
        SendMessage response = new SendMessage();
        String name = state.getParamByKey("contactName");
        Contact contact = service.findContactByName(chatId, name);

        if(contact != null) {
            response.setText("По имени " + name + " контакт успешно найден.");
            response.setReplyMarkup(inlineKeyboardCreator.createKeyboard(List.of(contact.getName()),
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
    private SendMessage handleFindContactByPhoneNumber(ContactService service, State state, Long chatId) {
        SendMessage response = new SendMessage();
        String number = state.getParamByKey("contactNumber");
        Contact contact = service.findContactByNumber(chatId, number);

        if(contact != null) {
            response.setText("По номеру " + number + " контакт успешно найден.");
            response.setReplyMarkup(inlineKeyboardCreator.createKeyboard(List.of(contact.getName()),
                    Operation.CURRENT_CONTACT_MENU.toString()));
        } else {
            state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
            response.setText("По номеру " + number + " контакты не найдены.");
            response.setReplyMarkup(replyKeyboardCreator.contactsMenu());
        }
        return response;
    }
}
