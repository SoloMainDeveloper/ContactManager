package org.example.repository;

import org.example.entity.Group;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий групп
 */
public interface IGroupRepository {
    /**
     * Добавить группу
     */
    void add(Group group);

    /**
     * Найти группу по названию, соответствующую конкретному пользователю по chatId
     */
    Optional<Group> findGroupByName(String name, Long chatId);

    /**
     * Найти все группы по id пользователя
     */
    List<Group> findGroupsByChatId(Long chatId, String sorter);

    /**
     * Обновить группу
     *
     * @param currentName имя группы до обновления
     */
    void update(String currentName, Group group);

    /**
     * Удалить группу по имени, соответствующую конкретному пользователю
     */
    void deleteByName(String name, Long chatId);
}
