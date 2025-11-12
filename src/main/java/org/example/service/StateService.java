package org.example.service;

import org.example.repository.StateRepository;
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
    private final StateRepository repository;

    /**
     * Конструктор
     */
    public StateService(StateRepository repository){
        this.repository = repository;
    }

    /**
     * Возвращает существующее состояние, в ином случае сначала его создаёт
     */
    private State getOrCreateState(Long chatId){
        if(!repository.containsKey(chatId)){
            repository.add(chatId, new State());
        }
        return repository.getStateById(chatId);
    }

    /**
     * Возвращает тип текущей операции у state по Id
     */
    public Operation getOperation(Long chatId){
        State state = getOrCreateState(chatId);
        return state.getOperation();
    }

    /**
     * Меняет значение текущей операции и очищает предыдущий контекст
     */
    public void changeCurrentOperation(
            Long chatId, Operation operation, boolean needClearContext) {
        State state = getOrCreateState(chatId);
        state.setOperation(operation, needClearContext);
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
    public void addParameter(Long chatId, String key, String value){
        State state = getOrCreateState(chatId);
        state.addParameter(key, value);
    }

    /**
     * Возвращает контекст операции у состояния
     */
    public Map<String, String> getParams(Long chatId) {
        return Collections.unmodifiableMap(getOrCreateState(chatId).getParams());
    }

    /**
     * Возвращает значение параметра контекста по его ключу
     */
    public String getParamByKey(Long chatId, String key) {
        return getOrCreateState(chatId).getParams().get(key);
    }
}
