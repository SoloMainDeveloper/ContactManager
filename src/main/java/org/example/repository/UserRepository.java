package org.example.repository;

import org.example.entity.User;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Репозиторий пользователей
 */
@Repository
public class UserRepository {
    /**
     * Хранилище пользователей
     */
    private final Map<Long, User> users = new LinkedHashMap<>();

    /**
     * Найти пользователя по Id
     */
    public User getUserById(Long chatId){
        if(!users.containsKey(chatId)){
            users.put(chatId, new User(chatId));
        }
        return users.get(chatId);
    }
}
