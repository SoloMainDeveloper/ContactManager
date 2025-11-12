package org.example.operations.group;

import org.example.keyboardcreator.ReplyKeyboardConstants;
import org.example.operations.OperationHandler;
import org.example.response.BotResponse;
import org.example.service.GroupService;
import org.example.service.StateService;
import org.example.state.Operation;
import org.springframework.stereotype.Component;

/**
 * Обработчик события: Добавление группы
 */
@Component
public class AddGroupHandler implements OperationHandler {
    /**
     * Создает текст для кнопок быстрого ввода команд
     */
    private final ReplyKeyboardConstants keyboardCreator = new ReplyKeyboardConstants();

    /**
     * Сервис групп
     */
    private final GroupService groupService;

    /**
     * Сервис состояний
     */
    private final StateService stateService;

    /**
     * Конструктор
     */
    public AddGroupHandler(GroupService groupService, StateService stateService) {
        this.groupService = groupService;
        this.stateService = stateService;
    }

    @Override
    public Operation getSupportedOperation() {
        return Operation.ADD_GROUP;
    }

    @Override
    public BotResponse handleMessage(Long chatId, String messageText) {
        BotResponse response = new BotResponse();
        switch (messageText){
            case "Добавить контакт" -> {
                //TODO
            }
            case "Сохранить группу" -> {
                String groupName = "Друзья";
                boolean isSuccessful = true; //groupService.tryAddGroup(chatId, params);
                if(isSuccessful){
                    response.setText("Группа " + groupName + " успешна сохранена");
                    response.setKeyboardText(keyboardCreator.addGroupMenu());
                } else {
                    response.setText("Группа не была сохранена");
                    //TODO почему не была сохранена
                }
                response.setKeyboardText(keyboardCreator.groupsMenu());
                stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU, true);
            }
            case "Назад" -> {
                response.setText("Вы вернулись назад");
                stateService.changeCurrentOperation(chatId, Operation.GROUPS_MENU, true);
                response.setKeyboardText(keyboardCreator.groupsMenu());
            }
            default -> {
                return handleMessageWithContext(chatId, messageText);
            }
        }
        return  response;
    }

    private BotResponse handleMessageWithContext(Long chatId, String messageText){
        //TODO
        BotResponse response = new BotResponse();
        String lastRequestedParamKey = stateService.getLastRequestedParamKey(chatId);
        if (lastRequestedParamKey == null) {
            response.setText("Я не понимаю эту команду.");
            return response;
        }
        stateService.addParameter(chatId, lastRequestedParamKey, messageText);
        response.setText("Отлично. Выберите какие контакты хотите добавить в группу");
        response.setKeyboardText(keyboardCreator.addGroupMenu());
        return response;
    }
}
