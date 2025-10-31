package org.example.operations;

import org.example.entity.Contact;
import org.example.keyboardcreator.ReplyKeyboardCreator;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

/**
 * Обработчик события: действия пользователя в меню текущего контакта
 */
public class CurrentContactMenuHandler implements OperationHandler {
    /**
     * Создает меню из кнопок для быстрого ввода команд
     */
    private final ReplyKeyboardCreator keyboardCreator = new ReplyKeyboardCreator();

    @Override
    public SendMessage handleMessage(ContactService service, State state, String messageText, Long chatId) {
        SendMessage response = new SendMessage();
        String contactName = state.getParamByKey("currentContactName");
        Contact contact = service.findContactByName(chatId, contactName);

        switch (messageText) {
            case "Меню пользователя вызвано":
                response.setText("Меню для пользователя " + contactName + " вызвано");
                response.setReplyMarkup(keyboardCreator.currentContactMenu());
                break;
            case "Информация":
                OperationHandler handler = Operation.GET_CONTACT_INFO.getHandler();
                return handler.handleMessage(service, state, contactName, chatId);
            case "Изменить":
                state.changeCurrentOperation(Operation.EDIT_CONTACT, false);
                response.setText("Отлично. Выберите какие данные хотите изменить у контакта");
                response.setReplyMarkup(keyboardCreator.editContactMenu());
                break;
            case "Блокировать":
                state.changeCurrentOperation(Operation.BLOCK_CONTACT, false);

                String isBlockedInfo = contact.isBlocked() ? "заблокирован" : "не заблокирован";
                String blockActionInfo = !contact.isBlocked() ? "заблокировать" : "разблокировать";
                String responseText = String.format("Текущий контакт %s. Вы хотите %s?", isBlockedInfo, blockActionInfo);

                response.setText(responseText);
                response.setReplyMarkup(keyboardCreator.createKeyboard(List.of("Да", "Нет")));
                break;
            case "Удалить":
                state.changeCurrentOperation(Operation.DELETE_CONTACT, false);
                response.setText("Вы точно хотите удалить текущий контакт?");
                response.setReplyMarkup(keyboardCreator.createKeyboard(List.of("Да", "Нет")));
                break;

            case "Назад":
                response.setText("Вы вернулись назад");
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                response.setReplyMarkup(keyboardCreator.contactsMenu());
                break;
            default:
                response.setText("Я не понимаю эту команду");
                break;
        }
        return response;
    }
}
