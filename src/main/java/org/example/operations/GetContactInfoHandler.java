package org.example.operations;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.keyboardcreator.ReplyKeyboardCreator;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

/**
 * Обработчик события: Получение информации о контакте
 */
public class GetContactInfoHandler implements OperationHandler {
    /**
     * Создает меню из кнопок для быстрого ввода команд
     */
    private final ReplyKeyboardCreator keyboardCreator = new ReplyKeyboardCreator();

    @Override
    public SendMessage handleMessage(ContactService service, State state, String messageText, Long chatId) {
        SendMessage response = new SendMessage();
        Contact contact = service.findContactByName(chatId, messageText);
        response.setText(getContactInfo(contact));
        response.setReplyMarkup(keyboardCreator.contactsMenu());
        state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
        return response;
    }

    /**
     * Возвращает информацию о пользователе в виде строки.
     */
    private String getContactInfo(Contact contact) {
        String phoneNumberInfo = contact.getPhoneNumber().isEmpty() ? "не указан" : contact.getPhoneNumber();
        String genderInfo = "";
        if (contact.getGender() == Gender.MALE) genderInfo = "мужской";
        if (contact.getGender() == Gender.FEMALE) genderInfo = "женский";
        if (contact.getGender() == Gender.NOT_SPECIFIED) genderInfo = "не указан";
        String ageInfo = contact.getAge() == -1 ? "не указан" : String.valueOf(contact.getAge());
        String isBlockedInfo = contact.isBlocked() ? "Заблокирован" : "Не заблокирован";

        return String.format("Контакт: %s\nНомер: %s\nПол: %s\nВозраст: %s\n%s",
                contact.getName(), phoneNumberInfo, genderInfo, ageInfo, isBlockedInfo);
    }
}
