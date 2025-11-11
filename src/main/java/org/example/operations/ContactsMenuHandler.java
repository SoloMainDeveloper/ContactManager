package org.example.operations;

import org.example.response.BotResponse;
import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

/**
 * Обработчик события: действия пользователя в меню контактов
 */
@Component
public class ContactsMenuHandler implements OperationHandler {
    /**
     * Создает меню из кнопок для быстрого ввода команд
     */
    private final ReplyKeyboardConstants keyboardCreator = new ReplyKeyboardConstants();

    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Конструктор
     */
    public ContactsMenuHandler(StateService stateService){
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.CONTACTS_MENU;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        switch (messageText) {
            case "Добавить":
                stateService.changeCurrentOperation(chatId, Operation.ADD_CONTACT, true);
                response.setText("Напишите имя добавляемого контакта");
                stateService.setLastRequestedParamKey(chatId,"contactName");
                break;
            case "Получить все":
                stateService.changeCurrentOperation(chatId,
                        Operation.GET_ALL_CONTACTS, true);
                response.setText("Желаете получить все контакты сразу или добавить" +
                        " фильтрацию/сортировку?");
                response.setKeyboardText(keyboardCreator.getAllContactsMenu());
                break;
            case "Найти":
                stateService.changeCurrentOperation(chatId,Operation.FIND_CONTACT, true);
                response.setText("Выберите по какому признаку будет произведен поиск");
                response.setKeyboardText(keyboardCreator.findContactMenu());
                break;
            case "Назад":
                response.setText("Вы вернулись назад");
                stateService.changeCurrentOperation(chatId,Operation.MAIN_MENU, true);
                response.setKeyboardText(keyboardCreator.mainMenu());
                break;
            default:
                response.setText("Я не понимаю эту команду");
                break;
        }
        return response;
    }
}
