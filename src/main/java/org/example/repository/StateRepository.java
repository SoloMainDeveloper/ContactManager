package org.example.repository;

import org.example.state.State;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Репозиторий состояний
 */
@Repository
public class StateRepository {
    /**
     * Хранилище состояний
     */
    private final Map<Long, State> states = new LinkedHashMap<>();

    /**
     * Найти состояние по Id
     */
    public State getStateById(Long chatId){
        return states.get(chatId);
    }

    /**
     * Существует ли состояние по данному chatId
     */
    public boolean containsKey(Long chatId){
        return states.containsKey(chatId);
    }

    /**
     * Добавляет состояние
     */
    public void add(Long chatId, State state) {
        states.put(chatId, state);
    }
}
