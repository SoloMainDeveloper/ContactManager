package org.example.operations;

import org.example.entity.Contact;
import org.example.keyboardcreator.InlineKeyboardCreator;
import org.example.keyboardcreator.ReplyKeyboardCreator;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.Objects;

public class GetAllContactsHandler implements OperationHandler {
    @Override
    public SendMessage handleMessage(ContactService service, State state, String messageText, Long chatId) {
        ReplyKeyboardCreator keyboardCreator = new ReplyKeyboardCreator();
        SendMessage response = new SendMessage();
        switch(messageText){
            case "Получить сразу":
                response.setText("Все контакты");
                List<Contact> contacts = service.findContactsByChatId(chatId);
                List<String> names = contacts.stream()
                        .map(Contact::getName)
                        .toList();
                response.setReplyMarkup(new InlineKeyboardCreator()
                        .createKeyboard(names, Operation.CURRENT_CONTACT_MENU.toString()));
                break;
            case "Добавить фильтр/сортировку":
                response.setText("Выберите, что хотите сделать");
                response.setReplyMarkup(keyboardCreator.createKeyboard(List.of("Добавить фильтр", "Сразу перейти к сортировке")));
                //
                state.setLastRequestedParamKey("choice");
                break;
            case "Добавить сортировку":
                response.setText("Выберите пол");
                response.setReplyMarkup(keyboardCreator.createKeyboard(List.of("Мужской", "Женский")));
                state.setLastRequestedParamKey("contactGender");
                break;
            case "Назад":
                response.setText("Вы вернулись назад");
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                response.setReplyMarkup(keyboardCreator.contactsMenu());
                break;
            default:
                return handleMessageWithContext(service, state, messageText, chatId);
        }
        return response;
    }

    private SendMessage handleMessageWithContext(ContactService service, State state, String messageText, Long chatId) {
        ReplyKeyboardCreator keyboardCreator = new ReplyKeyboardCreator();
        SendMessage response = new SendMessage();
        String lastRequestedParamKey = state.getLastRequestedParamKey();
        if (lastRequestedParamKey == null) {
            response.setText("Я не понимаю эту команду.");
            return response;
        }
        state.addParameter(lastRequestedParamKey, messageText);

        switch(lastRequestedParamKey){
            case "choice": {
                if(Objects.equals(messageText, "Добавить фильтр")) {
                    response.setText("Выберите фильтр");
                    response.setReplyMarkup(keyboardCreator.createKeyboard(List.of("По полу", "По возрасту")));
                    state.setLastRequestedParamKey("filter");
                }
                if(Objects.equals(messageText, "Сразу перейти к сортировке")) {
                    response.setText("Выберите в каком порядке выполнить сортировку");
                    response.setReplyMarkup(keyboardCreator.createKeyboard(
                            List.of("В порядке убывания возраста", "В порядке возрастания возраста",
                                    "В алфавитном порядке имени", "В обратном алфавитному порядку имени")));
                    state.setLastRequestedParamKey("sorter");
                }
                return response;
            }
            case "filter": {
                if(Objects.equals(messageText, "По полу")) {
                    response.setText("Выберите значение фильтра по полу");
                    response.setReplyMarkup(keyboardCreator.createKeyboard(List.of("Мужской", "Женский", "Не выбрано")));
                    state.setLastRequestedParamKey("filterByGender");
                }
                if(Objects.equals(messageText, "По возрасту")) {
                    response.setText("Введите значение фильтра по возрасту");
                    state.setLastRequestedParamKey("filterByAge");
                }
                return response;
            }
            case "filterByGender": {
                if(Objects.equals(messageText, "Мужской") ||
                        Objects.equals(messageText, "Женский") ||
                        Objects.equals(messageText, "Не выбрано")) {
                    response.setText("Отлично. Выбран следующий фильтр по полу: "
                            + messageText + ".\nНе желаете ли выбрать сортировку?");
                    state.addParameter("filterByGender", messageText);
                    response.setReplyMarkup(keyboardCreator.createKeyboard(List.of("Да", "Нет")));
                    state.setLastRequestedParamKey("needSortAfterFilterByGender");
                } else {
                    response.setText("Я не понимаю эту команду");
                    response.setReplyMarkup(keyboardCreator.getAllContactsMenu());
                }
                return response;
            }
            case "filterByAge": {
                try {
                    Number age = Integer.parseInt(messageText);
                    response.setText("Отлично. Теперь выберите условие фильтрации");
                    response.setReplyMarkup(keyboardCreator.createKeyboard(
                            List.of("> " + age, "< " + age, "= " + age)));
                    state.addParameter("ageValue", messageText);
                    state.setLastRequestedParamKey("filterByAgeCondition");
                } catch (NumberFormatException exception) {
                    response.setText("Произошла ошибка. Неверный формат возраста");
                    response.setReplyMarkup(keyboardCreator.getAllContactsMenu());
                }
                return response;
            }
            case "filterByAgeCondition": {
                String age = state.getParamByKey("ageValue");
                if(Objects.equals(messageText, "> " + age) ||
                        Objects.equals(messageText, "< " + age) ||
                        Objects.equals(messageText, "= " + age)) {
                    response.setText("Отлично. Выбран следующий фильтр по возрасту: "
                            + messageText + ".\nНе желаете ли выбрать сортировку?");
                    state.addParameter("filterByAge", messageText);
                    response.setReplyMarkup(keyboardCreator.createKeyboard(List.of("Да", "Нет")));
                    state.setLastRequestedParamKey("needSortAfterFilterByAge");
                } else {
                    response.setText("Я не понимаю эту команду");
                    response.setReplyMarkup(keyboardCreator.getAllContactsMenu());
                }
                return response;
            }
            case "needSortAfterFilterByGender": {
                if(Objects.equals(messageText, "Да")) {
                    response.setText("Выберите в каком порядке выполнить сортировку");
                    response.setReplyMarkup(keyboardCreator.createKeyboard(
                            List.of("В порядке убывания возраста", "В порядке возрастания возраста",
                                    "В алфавитном порядке имени", "В обратном алфавитному порядку имени")));
                    state.setLastRequestedParamKey("sorter");
                }
                if(Objects.equals(messageText, "Нет")) {
                    List<Contact> contacts = service.findContactsByChatIdAndGender(chatId, state.getParams());
                    if(contacts.isEmpty()) {
                        response.setText("Контакты не найдены с такой сортировкой");
                        response.setReplyMarkup(keyboardCreator.getAllContactsMenu());
                    } else {
                        response.setText("Все контакты с сортировкой по полу");
                        List<String> names = contacts.stream()
                                .map(Contact::getName)
                                .toList();
                        response.setReplyMarkup(new InlineKeyboardCreator()
                                .createKeyboard(names, Operation.CURRENT_CONTACT_MENU.toString()));
                    }
                }
                return response;
            }
            case "needSortAfterFilterByAge": {
                if(Objects.equals(messageText, "Да")) {
                    response.setText("Выберите в каком порядке выполнить сортировку");
                    response.setReplyMarkup(keyboardCreator.createKeyboard(
                            List.of("В порядке убывания возраста", "В порядке возрастания возраста",
                                    "В алфавитном порядке имени", "В обратном алфавитному порядку имени")));
                    state.setLastRequestedParamKey("sorter");
                }
                if(Objects.equals(messageText, "Нет")) {
                    List<Contact> contacts = service.findContactsByChatIdAndAge(chatId, state.getParams());
                    if(contacts.isEmpty()) {
                        response.setText("Контакты не найдены с такой сортировкой");
                        response.setReplyMarkup(keyboardCreator.getAllContactsMenu());
                    } else {
                        response.setText("Все контакты с сортировкой по возрасту");
                        List<String> names = contacts.stream()
                                .map(Contact::getName)
                                .toList();
                        response.setReplyMarkup(new InlineKeyboardCreator()
                                .createKeyboard(names, Operation.CURRENT_CONTACT_MENU.toString()));
                    }
                }
                return response;
            }
            case "sorter": {
                if(Objects.equals(messageText, "В порядке убывания возраста") ||
                        Objects.equals(messageText, "В порядке возрастания возраста") ||
                        Objects.equals(messageText, "В алфавитном порядке имени") ||
                        Objects.equals(messageText, "В обратном алфавитному порядку имени")) {
                    response.setText("Отлично. Выбрана следующая сортировка: " + messageText);
                    //Выполнить запрос на получение контактов
                } else {
                    response.setText("Я не понимаю эту команду");
                    response.setReplyMarkup(keyboardCreator.getAllContactsMenu());
                }
                return response;
            }
        }

        return response;
    }
}
