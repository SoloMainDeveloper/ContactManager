package org.example.state;

import java.util.LinkedHashMap;
import java.util.Map;

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
    private final Map<String, String> params = new LinkedHashMap<>();
    /**
     * Последний запрошенный ботом у пользователя параметр на ввод
     */
    private String lastRequestedParamKey;

    /**
     * Возвращает текущую операцию
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * Устанавливает текущую операцию
     */
    public void setOperation(Operation operation, boolean needClearContext) {
        this.operation = operation;
        if (needClearContext) {
            params.clear();
        }
    }

    /**
     * Возвращает параметры текущей функции
     */
    public Map<String, String> getParams() {
        return params;
    }

    /**
     * Добавляет параметр в контекст операции
     */
    public void addParameter(String key, String value) {
        params.put(key, value);
    }

    /**
     * Возвращает имя последнего запрошенного от пользователя параметра
     */
    public String getLastRequestedParamKey() {
        return lastRequestedParamKey;
    }

    /**
     * Устанавливает имя последнего запрошенного от пользователя параметра
     */
    public void setLastRequestedParamKey(String lastRequestedParamKey) {
        this.lastRequestedParamKey = lastRequestedParamKey;
    }
}
