package org.example.service;

import org.example.repository.GroupRepository;
import org.springframework.stereotype.Service;

/**
 * Сервис групп
 */
@Service
public class GroupService {
    /**
     * Репозиторий для групп
     */
    private final GroupRepository repository;

    /**
     * Конструктор. Инициализируем repository, создавая подключение к БД
     */
    public GroupService(GroupRepository repository){
        this.repository = repository;
    }
}
