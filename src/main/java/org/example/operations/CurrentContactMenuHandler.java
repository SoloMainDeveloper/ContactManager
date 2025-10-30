package org.example.operations;

import org.example.entity.Contact;
import org.example.entity.Gender;
import org.example.keyboardcreator.ReplyKeyboardCreator;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

public class CurrentContactMenuHandler implements OperationHandler {
    @Override
    public SendMessage handleMessage(ContactService service, State state, String messageText, Long chatId) {
        ReplyKeyboardCreator keyboardCreator = new ReplyKeyboardCreator();
        SendMessage response = new SendMessage();
        String contactName = state.getParamByKey("currentContact");
        Contact contact = service.findContactByName(chatId, contactName);
        switch (messageText) {
            case "Информация":
                response.setText(getContactInfo(contact));
                response.setReplyMarkup(keyboardCreator.currentContactMenu());
                break;
            case "Изменить":
                state.changeCurrentOperation(Operation.EDIT_CONTACT);
                state.addParameter("editContact", contactName);

                response.setText("Отлично. Выберите какие данные хотите изменить у контакта");
                response.setReplyMarkup(keyboardCreator.editContactMenu());
                break;
            case "Блокировать":
                state.changeCurrentOperation(Operation.BLOCK_CONTACT);

                String isBlockedInfo = contact.getBlocked() == true ? "заблокирован" : "не заблокирован";
                String blockActionInfo = contact.getBlocked() == false ? "заблокировать" : "разблокировать";
                String responseText = String.format("Текущий контакт %s. Вы хотите %s?", isBlockedInfo, blockActionInfo);

                response.setText(responseText);
                response.setReplyMarkup(keyboardCreator.createKeyboard(List.of("Да", "Нет")));
                break;
            case "Удалить":
                state.changeCurrentOperation(Operation.DELETE_CONTACT);
                response.setText("Вы точно хотите удалить текущий контакт?");
                response.setReplyMarkup(keyboardCreator.createKeyboard(List.of("Да", "Нет")));
                break;

            case "Назад":
                response.setText("Вы вернулись назад");
                state.changeCurrentOperation(Operation.CONTACTS_MENU);
                response.setReplyMarkup(keyboardCreator.contactsMenu());
                break;
            default:
                response.setText("Я не понимаю эту команду");
                break;
        }
        return response;
    }

    private String getContactInfo(Contact contact) {
        String phoneNumberInfo = contact.getPhoneNumber().isEmpty() ? "не указан" : contact.getPhoneNumber();
        String genderInfo = "";
        if (contact.getGender() == Gender.MALE) genderInfo = "мужской";
        if (contact.getGender() == Gender.FEMALE) genderInfo = "женский";
        if (contact.getGender() == Gender.NOT_SPECIFIED) genderInfo = "не указан";
        String ageInfo = contact.getAge() == -1 ? "не указан" : String.valueOf(contact.getAge());
        String isBlockedInfo = contact.getBlocked() == true ? "Заблокирован" : "Не заблокирован";

        return String.format("Контакт %s.\nНомер: %s.\nПол: %s.\nВозраст: %s.\n%s.",
                contact.getName(), phoneNumberInfo, genderInfo, ageInfo, isBlockedInfo);
    }
}
