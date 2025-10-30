package org.example.state;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Состояние диалога пользователя с ботом
 */
public class State {
    private Operation operation = Operation.MAIN_MENU;
    private final HashMap<String, String> params = new LinkedHashMap<>();
    private String lastRequestedParamKey;

    /**
     * Меняет значение текущей операции и очищает предыдущий контекст
     */
    public void changeCurrentOperation(Operation operation, boolean needClearContext) {
        this.operation = operation;
        if(needClearContext)
            this.params.clear();
    }

    public Operation getOperation() {
        return operation;
    }

    public String getLastRequestedParamKey() {
        return lastRequestedParamKey;
    }

    public void setLastRequestedParamKey(String lastRequestedParamKey) {
        this.lastRequestedParamKey = lastRequestedParamKey;
    }

    public void addParameter(String key, String value){
        params.put(key, value);
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public String getParamByKey(String contactName) {
        return params.get(contactName);
    }
}
