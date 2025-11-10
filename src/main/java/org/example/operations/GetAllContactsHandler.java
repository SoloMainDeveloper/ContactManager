package org.example.operations;

import org.example.response.BotResponse;
import org.example.entity.Contact;
import org.example.response.InlineKeyboardText;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.service.ContactService;
import org.example.state.Operation;
import org.example.state.State;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Обработчик события: Получение всех контактов
 */
@Component
public class GetAllContactsHandler implements OperationHandler {
    /**
     * Создает меню из кнопок для быстрого ввода команд
     */
    private final ReplyKeyboardConstants replyKeyboardCreator = new ReplyKeyboardConstants();

    /**
     * Сервис контактов
     */
    private final ContactService service;

    /**
     * Конструктор
     */
    public GetAllContactsHandler(ContactService service) {
        this.service = service;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.GET_ALL_CONTACTS;
    }

    @Override
    public BotResponse handleMessage(State state, String messageText, Long chatId) {
        BotResponse response = new BotResponse();

        switch(messageText){
            case "Получить" -> {
                response.setText("Все контакты");
                List<Contact> contacts = service.findContactsByChatId(chatId);
                List<String> names = contacts.stream()
                        .map(Contact::getName)
                        .toList();
                response.setInlineKeyboardText(new InlineKeyboardText(
                        names, Operation.CURRENT_CONTACT_MENU.toString()));
            }
            case "Добавить фильтр" -> {
                response.setText("Выберите фильтр");
                response.setReplyMarkup(replyKeyboardCreator.addFilterMenu());
                state.setLastRequestedParamKey("filter");
            }
            case "Добавить сортировку" -> {
                response.setText("Выберите в каком порядке выполнить сортировку");
                response.setReplyMarkup(replyKeyboardCreator.addSorterMenu());
                state.setLastRequestedParamKey("sorter");
            }
            case "Назад" -> {
                response.setText("Вы вернулись назад");
                state.changeCurrentOperation(Operation.CONTACTS_MENU, true);
                response.setReplyMarkup(replyKeyboardCreator.contactsMenu());
            }
            default -> response = handleMessageWithContext(state, messageText, chatId);
        }
        return response;
    }

    /**
     * Обрабатывает сообщение от пользователя. Заполняет контекст входными данными, которые были запрошены ботом, и затем
     * использует их для поиска.
     */
    private BotResponse handleMessageWithContext(State state, String messageText,
                                            Long chatId) {
        String lastRequestedParamKey = state.getLastRequestedParamKey();
        if (lastRequestedParamKey == null) {
            return new BotResponse("Я не понимаю эту команду.");
        }
        state.addParameter(lastRequestedParamKey, messageText);

        return switch (lastRequestedParamKey) {
            case "filter" -> handleFilterCommand(state, messageText);
            case "filterByGender" -> handleFilterByGenderCommand(state, messageText);
            case "filterByAge" -> handleFilterByAgeCommand(state, messageText);
            case "filterByAgeCondition" -> handleFilterByAgeConditionCommand(state, messageText);
            case "needSort" -> handleNeedSortCommand(state, messageText, chatId);
            case "sorter" -> handleSorterCommand(state, messageText, chatId);
            default -> new BotResponse("Я не понимаю эту команду.");
        };
    }

    /**
     * Обработать команду фильтрации
     */
    private BotResponse handleFilterCommand(State state, String messageText) {
        BotResponse response = new BotResponse();

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
    private BotResponse handleFilterByGenderCommand(State state, String messageText) {
        BotResponse response = new BotResponse();

        if(Objects.equals(messageText, "Мужской") ||
                Objects.equals(messageText, "Женский") ||
                Objects.equals(messageText, "Не выбрано")) {
            response.setText("Отлично. Выбран следующий фильтр по полу:"
                    + messageText + ".\nНе желаете ли выбрать сортировку?");
            state.addParameter("filterByGender", messageText);
            response.setReplyMarkup(List.of("Да", "Нет", "Назад к выбору"));
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
    private BotResponse handleFilterByAgeCommand(State state, String messageText) {
        BotResponse response = new BotResponse();
        try {
            if(Objects.equals(messageText, "Назад к выбору")) {
                response.setText("Вы вернулись назад к выбору");
                response.setReplyMarkup(replyKeyboardCreator.getAllContactsMenu());
                state.changeCurrentOperation(Operation.GET_ALL_CONTACTS, true);
            } else {
                Number age = Integer.parseInt(messageText);
                response.setText("Отлично. Теперь выберите условие фильтрации");
                response.setReplyMarkup(List.of(
                        "> " + age, "< " + age, "= " + age, "Назад к выбору"));
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
    private BotResponse handleFilterByAgeConditionCommand(State state, String messageText) {
        BotResponse response = new BotResponse();
        String age = state.getParamByKey("ageValue");

        if(Objects.equals(messageText, "> " + age) ||
                Objects.equals(messageText, "< " + age) ||
                Objects.equals(messageText, "= " + age)) {
            response.setText("Отлично. Выбран следующий фильтр по возрасту:"
                    + messageText + ".\nНе желаете ли выбрать сортировку?");
            state.addParameter("filterByAge", messageText);
            response.setReplyMarkup(List.of("Да", "Нет", "Назад к выбору"));
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
    private BotResponse handleNeedSortCommand(State state, String messageText, Long chatId) {
        BotResponse response = new BotResponse();

        if(Objects.equals(messageText, "Да")) {
            response.setReplyMarkup(replyKeyboardCreator.addSorterMenu());
            state.setLastRequestedParamKey("sorter");
            return new BotResponse("Выберите в каком порядке выполнить сортировку");
        }
        if(Objects.equals(messageText, "Нет")) {
            List<Contact> contacts = service.findContactsByChatIdWithFilterAndSorter(
                    chatId, state.getParams());
            if(contacts.isEmpty()) {
                response.setText("Контакты не найдены с такой фильтрацией");
                response.setReplyMarkup(replyKeyboardCreator.getAllContactsMenu());
            } else {
                response.setText("Все контакты с выбранной фильтрацией");
                List<String> names = contacts.stream()
                        .map(Contact::getName)
                        .toList();
                response.setInlineKeyboardText(new InlineKeyboardText(
                        names, Operation.CURRENT_CONTACT_MENU.toString()));
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
    private BotResponse handleSorterCommand(State state, String messageText, Long chatId) {
        BotResponse response = new BotResponse();

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
                response.setInlineKeyboardText(new InlineKeyboardText(
                        names, Operation.CURRENT_CONTACT_MENU.toString()));
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
