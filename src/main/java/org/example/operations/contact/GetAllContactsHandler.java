package org.example.operations.contact;

import org.example.entity.Gender;
import org.example.keyboardcreator.ReplyConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.entity.Contact;
import org.example.response.InlineKeyboardText;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.service.ContactService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.example.utils.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Обработчик события: Получение всех контактов
 */
@Component
public class GetAllContactsHandler implements OperationHandler {
    /**
     * Сервис контактов
     */
    private final ContactService contactService;

    /**
     * Сервис состояний
     */
    private final StateService stateService;

    private static final String FILTER = "filter";

    private static final String FILTER_BY_GENDER = "filterByGender";

    private static final String FILTER_BY_AGE = "filterByAge";

    private static final String AGE_VALUE = "ageValue";

    private static final String FILTER_BY_AGE_CONDITION = "filterByAgeCondition";

    private static final String CONDITION = "conditionValue";

    private static final String SORTER = "sorter";

    private static final String NEED_SORT = "needSort";

    /**
     * Конструктор
     */
    public GetAllContactsHandler(ContactService contactService, StateService stateService) {
        this.contactService = contactService;
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.GET_ALL_CONTACTS;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();

        switch (messageText) {
            case "Получить" -> {
                response.setText("Все контакты");
                List<Contact> contacts = contactService.findContactsByChatId(
                    chatId, ContactFilter.none(), ContactOrder.none());
                List<String> names = contacts.stream()
                        .map(Contact::getName)
                        .toList();
                response.setInlineKeyboardText(new InlineKeyboardText(
                        names, Operation.CURRENT_CONTACT_MENU.toString()));
            }
            case "Добавить фильтр" -> {
                response.setText("Выберите фильтр");
                response.setKeyboardText(ReplyKeyboardConstants.ADD_FILTER_MENU);
                stateService.setLastRequestedParamKey(chatId, FILTER);
            }
            case "Добавить сортировку" -> {
                response.setText("Выберите в каком порядке выполнить сортировку");
                response.setKeyboardText(ReplyKeyboardConstants.ADD_SORTER_CONTACT_MENU);
                stateService.setLastRequestedParamKey(chatId, SORTER);
            }
            case "Назад" -> {
                response.setText(ReplyConstants.COME_BACK);
                stateService.changeCurrentOperation(chatId, Operation.CONTACTS_MENU);
                response.setKeyboardText(ReplyKeyboardConstants.CONTACTS_MENU);
            }
            default -> response = handleMessageWithContext(chatId, messageText);
        }
        return response;
    }

    /**
     * Обрабатывает сообщение от пользователя. Заполняет контекст входными данными, которые были запрошены ботом, и затем
     * использует их для поиска.
     */
    private BotResponse handleMessageWithContext(Long chatId, String messageText) {
        String lastRequestedParamKey = stateService.getLastRequestedParamKey(chatId);
        if (lastRequestedParamKey == null) {
            return new BotResponse(ReplyConstants.UNKNOWN_COMMAND);
        }
        stateService.addParameter(chatId, lastRequestedParamKey, messageText);

        return switch (lastRequestedParamKey) {
            case FILTER -> handleFilterCommand(chatId, messageText);
            case FILTER_BY_GENDER -> handleFilterByGenderCommand(chatId, messageText);
            case FILTER_BY_AGE -> handleFilterByAgeCommand(chatId, messageText);
            case FILTER_BY_AGE_CONDITION ->
                    handleFilterByAgeConditionCommand(chatId, messageText);
            case NEED_SORT -> handleNeedSortCommand(chatId, messageText);
            case SORTER -> handleSorterCommand(chatId, messageText);
            default -> new BotResponse(ReplyConstants.UNKNOWN_COMMAND);
        };
    }

    /**
     * Обработать команду фильтрации
     */
    private BotResponse handleFilterCommand(Long chatId, String messageText) {
        BotResponse response = new BotResponse();

        switch (messageText) {
            case "По полу" -> {
                response.setText("Выберите значение фильтра по полу");
                response.setKeyboardText(ReplyKeyboardConstants.ADD_FILTER_BY_GENDER_MENU);
                stateService.setLastRequestedParamKey(chatId, FILTER_BY_GENDER);
            }
            case "По возрасту" -> {
                response.setText("Введите значение фильтра по возрасту");
                stateService.setLastRequestedParamKey(chatId, FILTER_BY_AGE);
            }
            case "Назад к выбору" -> {
                response.setText("Вы вернулись назад к выбору");
                response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_CONTACTS_MENU);
                stateService.changeCurrentOperation(chatId, Operation.GET_ALL_CONTACTS);
            }
            case null, default -> {
                response.setText(ReplyConstants.UNKNOWN_COMMAND);
                response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_CONTACTS_MENU);
            }
        }
        return response;
    }

    /**
     * Обработать команду фильтрации по полу
     */
    private BotResponse handleFilterByGenderCommand(Long chatId, String messageText) {
        BotResponse response = new BotResponse();

        if (Objects.equals(messageText, "Мужской") ||
                Objects.equals(messageText, "Женский") ||
                Objects.equals(messageText, "Не выбрано")) {
            response.setText("Отлично. Выбран следующий фильтр по полу: "
                    + messageText + ".\nНе желаете ли выбрать сортировку?");
            stateService.addParameter(chatId, FILTER_BY_GENDER, messageText);
            response.setKeyboardText(List.of("Да", "Нет", "Назад к выбору"));
            stateService.setLastRequestedParamKey(chatId, NEED_SORT);
        } else if (Objects.equals(messageText, "Назад к выбору")) {
            response.setText("Вы вернулись назад к выбору");
            response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_CONTACTS_MENU);
            stateService.changeCurrentOperation(chatId, Operation.GET_ALL_CONTACTS);
        } else {
            response.setText(ReplyConstants.UNKNOWN_COMMAND);
            response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_CONTACTS_MENU);
        }
        return response;
    }

    /**
     * Обработать команду фильтрации по возрасту
     */
    private BotResponse handleFilterByAgeCommand(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        try {
            if (Objects.equals(messageText, "Назад к выбору")) {
                response.setText("Вы вернулись назад к выбору");
                response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_CONTACTS_MENU);
                stateService.changeCurrentOperation(chatId, Operation.GET_ALL_CONTACTS);
            } else {
                int age = Integer.parseInt(messageText);
                response.setText("Отлично. Теперь выберите условие фильтрации");
                response.setKeyboardText(List.of(
                        "> " + age, "< " + age, "= " + age, "Назад к выбору"));
                stateService.addParameter(chatId, AGE_VALUE, String.valueOf(age));
                stateService.setLastRequestedParamKey(chatId, FILTER_BY_AGE_CONDITION);
            }
        } catch (NumberFormatException exception) {
            response.setText("Произошла ошибка. Неверный формат возраста");
            response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_CONTACTS_MENU);
        }
        return response;
    }

    /**
     * Обработать текст сообщения с заполненным условием фильтрации по возрасту
     */
    private BotResponse handleFilterByAgeConditionCommand(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        String age = (String) stateService.getParamByKey(chatId, AGE_VALUE);
        ContactFilter.Condition condition = null;
        boolean isCorrectFilter = false;

        if (Objects.equals(messageText, "> " + age)) {
            condition = ContactFilter.Condition.GREATER_THAN;
            isCorrectFilter = true;
        }
        if (Objects.equals(messageText, "< " + age)) {
            condition = ContactFilter.Condition.LESS_THAN;
            isCorrectFilter = true;
        }
        if (Objects.equals(messageText, "= " + age)) {
            condition = ContactFilter.Condition.EQUALS;
            isCorrectFilter = true;
        }


        if (isCorrectFilter) {
            response.setText("Отлично. Выбран следующий фильтр по возрасту:"
                    + messageText + ".\nНе желаете ли выбрать сортировку?");
            stateService.addParameter(chatId, CONDITION, condition);
            response.setKeyboardText(List.of("Да", "Нет", "Назад к выбору"));
            stateService.setLastRequestedParamKey(chatId, NEED_SORT);
        } else if (Objects.equals(messageText, "Назад к выбору")) {
            response.setText("Вы вернулись назад к выбору");
            response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_CONTACTS_MENU);
            stateService.changeCurrentOperation(chatId, Operation.GET_ALL_CONTACTS);
        } else {
            response.setText(ReplyConstants.UNKNOWN_COMMAND);
            response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_CONTACTS_MENU);
        }
        return response;
    }

    /**
     * Обработать команду необходимости сортировки после применения фильтрации
     */
    private BotResponse handleNeedSortCommand(Long chatId, String messageText) {
        BotResponse response = new BotResponse();

        switch (messageText) {
            case "Да" -> {
                response.setKeyboardText(ReplyKeyboardConstants.ADD_SORTER_CONTACT_MENU);
                stateService.setLastRequestedParamKey(chatId, SORTER);
                response.setText("Выберите в каком порядке выполнить сортировку");
            }
            case "Нет" -> {
                ContactFilter filter = createFilterFromContext(stateService.getParams(chatId));
                ContactOrder order = createOrderFromMessageText(messageText);
                List<Contact> contacts = contactService
                        .findContactsByChatId(chatId, filter, order);
                if (contacts.isEmpty()) {
                    response.setText("Контакты не найдены с такой фильтрацией");
                    response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_CONTACTS_MENU);
                } else {
                    response.setText("Все контакты с выбранной фильтрацией");
                    List<String> names = contacts.stream()
                            .map(Contact::getName)
                            .toList();
                    response.setInlineKeyboardText(new InlineKeyboardText(
                            names, Operation.CURRENT_CONTACT_MENU.toString()));
                }
            }
            case "Назад к выбору" -> {
                response.setText("Вы вернулись назад к выбору");
                response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_CONTACTS_MENU);
                stateService.changeCurrentOperation(chatId, Operation.GET_ALL_CONTACTS);
            }
            case null, default -> {
                response.setText(ReplyConstants.UNKNOWN_COMMAND);
                response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_CONTACTS_MENU);
            }
        }
        return response;
    }

    /**
     * Обработать команду сортировки
     */
    private BotResponse handleSorterCommand(Long chatId, String messageText) {
        BotResponse response = new BotResponse();

        if (Objects.equals(messageText, "В порядке убывания возраста") ||
                Objects.equals(messageText, "В порядке возрастания возраста") ||
                Objects.equals(messageText, "В алфавитном порядке имени") ||
                Objects.equals(messageText, "В обратном алфавитному порядку имени")) {
            response.setText("Отлично. Выбрана следующая сортировка: " + messageText);
            stateService.addParameter(chatId, SORTER, messageText);
            ContactFilter filter = createFilterFromContext(stateService.getParams(chatId));
            ContactOrder order = createOrderFromMessageText(messageText);
            List<Contact> contacts = contactService
                .findContactsByChatId(chatId, filter, order);
            if (contacts.isEmpty()) {
                response.setText("Контакты не найдены с такой фильтрацией");
                response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_CONTACTS_MENU);
            } else {
                response.setText("Все контакты с выбранной сортировкой");
                List<String> names = contacts.stream()
                        .map(Contact::getName)
                        .toList();
                response.setInlineKeyboardText(new InlineKeyboardText(
                        names, Operation.CURRENT_CONTACT_MENU.toString()));
            }
        } else if (Objects.equals(messageText, "Назад к выбору")) {
            response.setText("Вы вернулись назад к выбору");
            response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_CONTACTS_MENU);
            stateService.changeCurrentOperation(chatId, Operation.GET_ALL_CONTACTS);
        } else {
            response.setText(ReplyConstants.UNKNOWN_COMMAND);
            response.setKeyboardText(ReplyKeyboardConstants.GET_ALL_CONTACTS_MENU);
        }
        return response;
    }

    /**
     * Создать фильтр из контекста на основе заданных пользователем параметров
     */
    private ContactFilter createFilterFromContext(Map<String, Object> params) {
        if (params.containsKey(FILTER_BY_GENDER)) {
            String genderValue = Gender.fromDisplayName(
                (String) params.get(FILTER_BY_GENDER)).name();
            return new ContactFilter(ContactFilter.FilterProperty.GENDER,
                ContactFilter.Condition.EQUALS, genderValue);
        }
        if (params.containsKey(FILTER_BY_AGE)) {
            ContactFilter.Condition ageCondition =
                (ContactFilter.Condition) params.get(CONDITION);
            String ageValue = (String) params.get(AGE_VALUE);
            return new ContactFilter(ContactFilter.FilterProperty.AGE,
                ageCondition, ageValue);
        }
        return ContactFilter.none();
    }

    /**
     * Создать порядок сортировки из текста сообщения
     */
    private ContactOrder createOrderFromMessageText(String message) {
        return switch (message) {
            case "В порядке возрастания возраста" ->
                new ContactOrder(ContactOrder.OrderProperty.AGE, ContactOrder.Direction.ASC);
            case "В порядке убывания возраста" ->
                new ContactOrder(ContactOrder.OrderProperty.AGE, ContactOrder.Direction.DESC);
            case "В алфавитном порядке имени" ->
                new ContactOrder(ContactOrder.OrderProperty.NAME, ContactOrder.Direction.ASC);
            case "В обратном алфавитному порядку имени" ->
                new ContactOrder(ContactOrder.OrderProperty.NAME, ContactOrder.Direction.DESC);
            default -> ContactOrder.none();
        };
    }
}
