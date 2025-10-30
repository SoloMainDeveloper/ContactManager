package org.example.state;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Состояние диалога пользователя с ботом
 */
public class State {
    /**
     * Текущая операция
     */
    private Operation operation = Operation.MAIN_MENU;
    /**
     * Контекст операции
     */
    private final HashMap<String, String> params = new LinkedHashMap<>();
    /**
     * Последний запрошенный ботом у пользователя параметр на ввод
     */
    private String lastRequestedParamKey;

    /**
     * Меняет значение текущей операции и очищает предыдущий контекст
     */
    public void changeCurrentOperation(Operation operation, boolean needClearContext) {
        this.operation = operation;
        if(needClearContext)
            this.params.clear();
    }

    /**
     * Возвращает текущую операцию
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * Возвращает последний запрошенный ботом параметр
     */
    public String getLastRequestedParamKey() {
        return lastRequestedParamKey;
    }

    /**
     * Устанавливает ключ последнего запрошенного параметра
     */
    public void setLastRequestedParamKey(String lastRequestedParamKey) {
        this.lastRequestedParamKey = lastRequestedParamKey;
    }

    /**
     * Добавление параметра в контекст
     */
    public void addParameter(String key, String value){
        params.put(key, value);
    }

    /**
     * Возвращает контекст операции
     */
    public HashMap<String, String> getParams() {
        return params;
    }

    /**
     * Возвращает значение параметра контекста по его ключу
     */
    public String getParamByKey(String contactName) {
        return params.get(contactName);
    }
}
