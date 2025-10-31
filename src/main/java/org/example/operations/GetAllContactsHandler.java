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

/**
 * Обработчик события: Получение всех контактов
 */
public class GetAllContactsHandler implements OperationHandler {
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
            case "Получить":
                response.setText("Все контакты");
                List<Contact> contacts = service.findContactsByChatId(chatId);
                List<String> names = contacts.stream()
                        .map(Contact::getName)
                        .toList();
                response.setReplyMarkup(inlineKeyboardCreator
                        .createKeyboard(names, Operation.CURRENT_CONTACT_MENU.toString()));
                break;
            case "Добавить фильтр":
                response.setText("Выберите фильтр");
                response.setReplyMarkup(replyKeyboardCreator.addFilterMenu());
                state.setLastRequestedParamKey("filter");
                break;
            case "Добавить сортировку":
                response.setText("Выберите в каком порядке выполнить сортировку");
                response.setReplyMarkup(replyKeyboardCreator.addSorterMenu());
                state.setLastRequestedParamKey("sorter");
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
     * использует их для поиска.
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
            case "filter" -> handleFilterCommand(state, messageText);
            case "filterByGender" -> handleFilterByGenderCommand(state, messageText);
            case "filterByAge" -> handleFilterByAgeCommand(state, messageText);
            case "filterByAgeCondition" -> handleFilterByAgeConditionCommand(state, messageText);
            case "needSort" -> handleNeedSortCommand(service, state, messageText, chatId);
            case "sorter" -> handleSorterCommand(service, state, messageText, chatId);
            default -> response;
        };
    }

    /**
     * Обработать команду фильтрации
     */
    private SendMessage handleFilterCommand(State state, String messageText) {
        SendMessage response = new SendMessage();

        switch (messageText) {
            case "По полу" -> {
                response.setText("Выберите значение фильтра по полу");
                response.setReplyMarkup(replyKeyboardCreator.addFilterByGenderMenu());
                state.setLastRequestedParamKey("filterByGender");
            }
            case "По возрасту" -> {
                response.setText("Введите значение фильтра по возрасту");
                state.setLastRequestedParamKey("filterByAge");
            }
            case "Назад к выбору" -> {
                response.setText("Вы вернулись назад к выбору");
                response.setReplyMarkup(replyKeyboardCreator.getAllContactsMenu());
                state.changeCurrentOperation(Operation.GET_ALL_CONTACTS, true);
            }
            case null, default -> {
                response.setText("Я не понимаю эту команду");
                response.setReplyMarkup(replyKeyboardCreator.getAllContactsMenu());
            }
        }
        return response;
    }

    /**
     * Обработать команду фильтрации по полу
     */
    private SendMessage handleFilterByGenderCommand(State state, String messageText) {
        SendMessage response = new SendMessage();

        if(Objects.equals(messageText, "Мужской") ||
                Objects.equals(messageText, "Женский") ||
                Objects.equals(messageText, "Не выбрано")) {
            response.setText("Отлично. Выбран следующий фильтр по полу: "
                    + messageText + ".\nНе желаете ли выбрать сортировку?");
            state.addParameter("filterByGender", messageText);
            response.setReplyMarkup(replyKeyboardCreator.createKeyboard(List.of("Да", "Нет", "Назад к выбору")));
            state.setLastRequestedParamKey("needSort");
        } else if(Objects.equals(messageText, "Назад к выбору")) {
            response.setText("Вы вернулись назад к выбору");
            response.setReplyMarkup(replyKeyboardCreator.getAllContactsMenu());
            state.changeCurrentOperation(Operation.GET_ALL_CONTACTS, true);
        } else {
            response.setText("Я не понимаю эту команду");
            response.setReplyMarkup(replyKeyboardCreator.getAllContactsMenu());
        }
        return response;
    }

    /**
     * Обработать команду фильтрации по возрасту
     */
    private SendMessage handleFilterByAgeCommand(State state, String messageText) {
        SendMessage response = new SendMessage();
        try {
            if(Objects.equals(messageText, "Назад к выбору")) {
                response.setText("Вы вернулись назад к выбору");
                response.setReplyMarkup(replyKeyboardCreator.getAllContactsMenu());
                state.changeCurrentOperation(Operation.GET_ALL_CONTACTS, true);
            } else {
                Number age = Integer.parseInt(messageText);
                response.setText("Отлично. Теперь выберите условие фильтрации");
                response.setReplyMarkup(replyKeyboardCreator.createKeyboard(
                        List.of("> " + age, "< " + age, "= " + age, "Назад к выбору")));
                state.addParameter("ageValue", messageText);
                state.setLastRequestedParamKey("filterByAgeCondition");
            }
        } catch (NumberFormatException exception) {
            response.setText("Произошла ошибка. Неверный формат возраста");
            response.setReplyMarkup(replyKeyboardCreator.getAllContactsMenu());
        }
        return response;
    }

    /**
     * Обработать текст сообщения с заполненным условием фильтрации по возрасту
     */
    private SendMessage handleFilterByAgeConditionCommand(State state, String messageText) {
        SendMessage response = new SendMessage();
        String age = state.getParamByKey("ageValue");

        if(Objects.equals(messageText, "> " + age) ||
                Objects.equals(messageText, "< " + age) ||
                Objects.equals(messageText, "= " + age)) {
            response.setText("Отлично. Выбран следующий фильтр по возрасту: "
                    + messageText + ".\nНе желаете ли выбрать сортировку?");
            state.addParameter("filterByAge", messageText);
            response.setReplyMarkup(replyKeyboardCreator.createKeyboard(List.of("Да", "Нет", "Назад к выбору")));
            state.setLastRequestedParamKey("needSort");
        } else if(Objects.equals(messageText, "Назад к выбору")) {
            response.setText("Вы вернулись назад к выбору");
            response.setReplyMarkup(replyKeyboardCreator.getAllContactsMenu());
            state.changeCurrentOperation(Operation.GET_ALL_CONTACTS, true);
        } else {
            response.setText("Я не понимаю эту команду");
            response.setReplyMarkup(replyKeyboardCreator.getAllContactsMenu());
        }
        return response;
    }

    /**
     * Обработать команду необходимости сортировки после применения фильтрации
     */
    private SendMessage handleNeedSortCommand(ContactService service, State state, String messageText, Long chatId) {
        SendMessage response = new SendMessage();

        if(Objects.equals(messageText, "Да")) {
            response.setText("Выберите в каком порядке выполнить сортировку");
            response.setReplyMarkup(replyKeyboardCreator.addSorterMenu());
            state.setLastRequestedParamKey("sorter");
        }
        if(Objects.equals(messageText, "Нет")) {
            List<Contact> contacts = service.findContactsByChatIdWithFilterAndSorter(chatId, state.getParams());
            if(contacts.isEmpty()) {
                response.setText("Контакты не найдены с такой фильтрацией");
                response.setReplyMarkup(replyKeyboardCreator.getAllContactsMenu());
            } else {
                response.setText("Все контакты с выбранной фильтрацией");
                List<String> names = contacts.stream()
                        .map(Contact::getName)
                        .toList();
                response.setReplyMarkup(inlineKeyboardCreator
                        .createKeyboard(names, Operation.CURRENT_CONTACT_MENU.toString()));
            }
        } else if(Objects.equals(messageText, "Назад к выбору")) {
            response.setText("Вы вернулись назад к выбору");
            response.setReplyMarkup(replyKeyboardCreator.getAllContactsMenu());
            state.changeCurrentOperation(Operation.GET_ALL_CONTACTS, true);
        } else {
            response.setText("Я не понимаю эту команду");
            response.setReplyMarkup(replyKeyboardCreator.getAllContactsMenu());
        }
        return response;
    }

    /**
     * Обработать команду сортировки
     */
    private SendMessage handleSorterCommand(ContactService service, State state, String messageText, Long chatId) {
        SendMessage response = new SendMessage();

        if(Objects.equals(messageText, "В порядке убывания возраста") ||
                Objects.equals(messageText, "В порядке возрастания возраста") ||
                Objects.equals(messageText, "В алфавитном порядке имени") ||
                Objects.equals(messageText, "В обратном алфавитному порядку имени")) {
            response.setText("Отлично. Выбрана следующая сортировка: " + messageText);
            state.addParameter("sorter", messageText);
            List<Contact> contacts = service.findContactsByChatIdWithFilterAndSorter(chatId, state.getParams());
            if(contacts.isEmpty()) {
                response.setText("Контакты не найдены с такой фильтрацией");
                response.setReplyMarkup(replyKeyboardCreator.getAllContactsMenu());
            } else {
                response.setText("Все контакты с выбранной сортировкой");
                List<String> names = contacts.stream()
                        .map(Contact::getName)
                        .toList();
                response.setReplyMarkup(inlineKeyboardCreator
                        .createKeyboard(names, Operation.CURRENT_CONTACT_MENU.toString()));
            }
        } else if(Objects.equals(messageText, "Назад к выбору")) {
            response.setText("Вы вернулись назад к выбору");
            response.setReplyMarkup(replyKeyboardCreator.getAllContactsMenu());
            state.changeCurrentOperation(Operation.GET_ALL_CONTACTS, true);
        } else {
            response.setText("Я не понимаю эту команду");
            response.setReplyMarkup(replyKeyboardCreator.getAllContactsMenu());
        }
        return response;
    }
}
