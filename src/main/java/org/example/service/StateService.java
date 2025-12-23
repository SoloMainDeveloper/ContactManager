package org.example.service;

import org.example.state.InMemoryStateStorage;
import org.example.state.Operation;
import org.example.state.State;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * Сервис состояний
 */
@Service
public class StateService {
    /**
     * Репозиторий состояний
     */
    private final InMemoryStateStorage inMemoryStateStorage;

    public StateService() {
        this.inMemoryStateStorage = new InMemoryStateStorage();
    }

    /**
     * Возвращает существующее состояние, в ином случае сначала его создаёт
     */
    private State getOrCreateState(Long chatId) {
        if (!inMemoryStateStorage.containsKey(chatId)) {
            inMemoryStateStorage.add(chatId, new State());
        }
        return inMemoryStateStorage.getStateById(chatId);
    }

    /**
     * Возвращает тип текущей операции у state по Id
     */
    public Operation getOperation(Long chatId) {
        State state = getOrCreateState(chatId);
        return state.getOperation();
    }

    /**
     * Меняет значение текущей операции и очищает предыдущий
     * контекст, если необходимо для данной операции
     */
    public void changeCurrentOperation(Long chatId, Operation operation) {
        State state = getOrCreateState(chatId);
        state.setOperation(operation);
    }

    /**
     * Возвращает последний запрошенный ботом параметр
     */
    public String getLastRequestedParamKey(Long chatId) {
        State state = getOrCreateState(chatId);
        return state.getLastRequestedParamKey();
    }

    /**
     * Устанавливает ключ последнего запрошенного параметра
     */
    public void setLastRequestedParamKey(Long chatId, String lastRequestedParamKey) {
        State state = getOrCreateState(chatId);
        state.setLastRequestedParamKey(lastRequestedParamKey);
    }

    /**
     * Добавление параметра в контекст состояния
     */
    public void addParameter(Long chatId, String key, Object value) {
        State state = getOrCreateState(chatId);
        state.addParameter(key, value);
    }

    /**
     * Возвращает контекст операции у состояния
     */
    public Map<String, Object> getParams(Long chatId) {
        return Collections.unmodifiableMap(getOrCreateState(chatId).getParams());
    }

    /**
     * Возвращает значение параметра контекста по его ключу
     */
    public Object getParamByKey(Long chatId, String key) {
        return getOrCreateState(chatId).getParams().get(key);
    }
}
