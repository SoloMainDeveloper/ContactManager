package org.example.operations.contact;

import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.entity.Contact;
import org.example.response.InlineKeyboardText;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.service.ContactService;
import org.example.service.StateService;
import org.example.state.Operation;
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
    private final ContactService contactService;

    /**
     * Сервис состояний
     */
    private final StateService stateService;

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

        switch(messageText){
            case "Получить" -> {
                response.setText("Все контакты");
                List<Contact> contacts = contactService.findContactsByChatId(chatId);
                List<String> names = contacts.stream()
                        .map(Contact::getName)
                        .toList();
                response.setInlineKeyboardText(new InlineKeyboardText(
                        names, Operation.CURRENT_CONTACT_MENU.toString()));
            }
            case "Добавить фильтр" -> {
                response.setText("Выберите фильтр");
                response.setKeyboardText(replyKeyboardCreator.addFilterMenu());
                stateService.setLastRequestedParamKey(chatId, "filter");
            }
            case "Добавить сортировку" -> {
                response.setText("Выберите в каком порядке выполнить сортировку");
                response.setKeyboardText(replyKeyboardCreator.addSorterMenu());
                stateService.setLastRequestedParamKey(chatId, "sorter");
            }
            case "Назад" -> {
                response.setText("Вы вернулись назад");
                stateService.changeCurrentOperation(
                        chatId, Operation.CONTACTS_MENU, true);
                response.setKeyboardText(replyKeyboardCreator.contactsMenu());
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
            return new BotResponse("Я не понимаю эту команду.");
        }
        stateService.addParameter(chatId, lastRequestedParamKey, messageText);

        return switch (lastRequestedParamKey) {
            case "filter" -> handleFilterCommand(chatId, messageText);
            case "filterByGender" -> handleFilterByGenderCommand(chatId, messageText);
            case "filterByAge" -> handleFilterByAgeCommand(chatId, messageText);
            case "filterByAgeCondition" ->
                    handleFilterByAgeConditionCommand(chatId, messageText);
            case "needSort" -> handleNeedSortCommand(chatId, messageText);
            case "sorter" -> handleSorterCommand(chatId, messageText);
            default -> new BotResponse("Я не понимаю эту команду.");
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
                response.setKeyboardText(replyKeyboardCreator.addFilterByGenderMenu());
                stateService.setLastRequestedParamKey(chatId, "filterByGender");
            }
            case "По возрасту" -> {
                response.setText("Введите значение фильтра по возрасту");
                stateService.setLastRequestedParamKey(chatId, "filterByAge");
            }
            case "Назад к выбору" -> {
                response.setText("Вы вернулись назад к выбору");
                response.setKeyboardText(replyKeyboardCreator.getAllContactsMenu());
                stateService.changeCurrentOperation(
                        chatId, Operation.GET_ALL_CONTACTS, true);
            }
            case null, default -> {
                response.setText("Я не понимаю эту команду");
                response.setKeyboardText(replyKeyboardCreator.getAllContactsMenu());
            }
        }
        return response;
    }

    /**
     * Обработать команду фильтрации по полу
     */
    private BotResponse handleFilterByGenderCommand(Long chatId, String messageText) {
        BotResponse response = new BotResponse();

        if(Objects.equals(messageText, "Мужской") ||
                Objects.equals(messageText, "Женский") ||
                Objects.equals(messageText, "Не выбрано")) {
            response.setText("Отлично. Выбран следующий фильтр по полу: "
                    + messageText + ".\nНе желаете ли выбрать сортировку?");
            stateService.addParameter(chatId, "filterByGender", messageText);
            response.setKeyboardText(List.of("Да", "Нет", "Назад к выбору"));
            stateService.setLastRequestedParamKey(chatId, "needSort");
        } else if(Objects.equals(messageText, "Назад к выбору")) {
            response.setText("Вы вернулись назад к выбору");
            response.setKeyboardText(replyKeyboardCreator.getAllContactsMenu());
            stateService.changeCurrentOperation(chatId, Operation.GET_ALL_CONTACTS, true);
        } else {
            response.setText("Я не понимаю эту команду");
            response.setKeyboardText(replyKeyboardCreator.getAllContactsMenu());
        }
        return response;
    }

    /**
     * Обработать команду фильтрации по возрасту
     */
    private BotResponse handleFilterByAgeCommand(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        try {
            if(Objects.equals(messageText, "Назад к выбору")) {
                response.setText("Вы вернулись назад к выбору");
                response.setKeyboardText(replyKeyboardCreator.getAllContactsMenu());
                stateService.changeCurrentOperation(
                        chatId, Operation.GET_ALL_CONTACTS, true);
            } else {
                Number age = Integer.parseInt(messageText);
                response.setText("Отлично. Теперь выберите условие фильтрации");
                response.setKeyboardText(List.of(
                        "> " + age, "< " + age, "= " + age, "Назад к выбору"));
                stateService.addParameter(chatId, "ageValue", messageText);
                stateService.setLastRequestedParamKey(chatId, "filterByAgeCondition");
            }
        } catch (NumberFormatException exception) {
            response.setText("Произошла ошибка. Неверный формат возраста");
            response.setKeyboardText(replyKeyboardCreator.getAllContactsMenu());
        }
        return response;
    }

    /**
     * Обработать текст сообщения с заполненным условием фильтрации по возрасту
     */
    private BotResponse handleFilterByAgeConditionCommand(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        String age = stateService.getParamByKey(chatId, "ageValue");

        if(Objects.equals(messageText, "> " + age) ||
                Objects.equals(messageText, "< " + age) ||
                Objects.equals(messageText, "= " + age)) {
            response.setText("Отлично. Выбран следующий фильтр по возрасту:"
                    + messageText + ".\nНе желаете ли выбрать сортировку?");
            stateService.addParameter(chatId, "filterByAge", messageText);
            response.setKeyboardText(List.of("Да", "Нет", "Назад к выбору"));
            stateService.setLastRequestedParamKey(chatId, "needSort");
        } else if(Objects.equals(messageText, "Назад к выбору")) {
            response.setText("Вы вернулись назад к выбору");
            response.setKeyboardText(replyKeyboardCreator.getAllContactsMenu());
            stateService.changeCurrentOperation(chatId, Operation.GET_ALL_CONTACTS, true);
        } else {
            response.setText("Я не понимаю эту команду");
            response.setKeyboardText(replyKeyboardCreator.getAllContactsMenu());
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
                response.setKeyboardText(replyKeyboardCreator.addSorterMenu());
                stateService.setLastRequestedParamKey(chatId, "sorter");
                response.setText("Выберите в каком порядке выполнить сортировку");
            }
            case "Нет" -> {
                List<Contact> contacts = contactService.findContactsByChatIdWithFilterAndSorter(
                        chatId, stateService.getParams(chatId));
                if (contacts.isEmpty()) {
                    response.setText("Контакты не найдены с такой фильтрацией");
                    response.setKeyboardText(replyKeyboardCreator.getAllContactsMenu());
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
                response.setKeyboardText(replyKeyboardCreator.getAllContactsMenu());
                stateService.changeCurrentOperation(chatId, Operation.GET_ALL_CONTACTS, true);
            }
            case null, default -> {
                response.setText("Я не понимаю эту команду");
                response.setKeyboardText(replyKeyboardCreator.getAllContactsMenu());
            }
        }
        return response;
    }

    /**
     * Обработать команду сортировки
     */
    private BotResponse handleSorterCommand(Long chatId, String messageText) {
        BotResponse response = new BotResponse();

        if(Objects.equals(messageText, "В порядке убывания возраста") ||
                Objects.equals(messageText, "В порядке возрастания возраста") ||
                Objects.equals(messageText, "В алфавитном порядке имени") ||
                Objects.equals(messageText, "В обратном алфавитному порядку имени")) {
            response.setText("Отлично. Выбрана следующая сортировка: " + messageText);
            stateService.addParameter(chatId, "sorter", messageText);
            List<Contact> contacts = contactService
                    .findContactsByChatIdWithFilterAndSorter(chatId,
                            stateService.getParams(chatId));
            if(contacts.isEmpty()) {
                response.setText("Контакты не найдены с такой фильтрацией");
                response.setKeyboardText(replyKeyboardCreator.getAllContactsMenu());
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
            response.setKeyboardText(replyKeyboardCreator.getAllContactsMenu());
            stateService.changeCurrentOperation(chatId, Operation.GET_ALL_CONTACTS, true);
        } else {
            response.setText("Я не понимаю эту команду");
            response.setKeyboardText(replyKeyboardCreator.getAllContactsMenu());
        }
        return response;
    }
}
