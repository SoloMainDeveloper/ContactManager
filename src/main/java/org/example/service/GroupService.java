package org.example.service;

import org.example.entity.Group;
import org.example.exceptions.GroupAlreadyExistsException;
import org.example.exceptions.GroupDoesNotExistException;
import org.example.repository.IGroupRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Сервис групп
 */
@Service
public class GroupService {
    /**
     * Репозиторий для групп
     */
    private final IGroupRepository repository;

    /**
     * Конструктор. Инициализируем repository, создавая подключение к БД
     */
    public GroupService(IGroupRepository repository) {
        this.repository = repository;
    }

    /**
     * Попытаться добавить группу
     *
     * @param chatId идентификатор чата
     * @param group добавляемая группы
     * @throws GroupAlreadyExistsException если группа уже существует
     */
    public void tryAddGroup(Long chatId, Group group)
            throws GroupAlreadyExistsException {
        String groupName = group.getName();
        if (findGroupByName(chatId, groupName).isEmpty()) {
            repository.add(group);
        } else {
            throw new GroupAlreadyExistsException(
                    "Группа %s уже существует".formatted(groupName));
        }
    }

    /**
     * Найти контакт по имени у данного пользователя
     */
    public Optional<Group> findGroupByName(Long chatId, String groupName) {
        return repository.findGroupByName(groupName, chatId);
    }

    /**
     * Найти все контакты, имеющееся у данного пользователя
     */
    public List<Group> findGroupsByChatId(Long chatId) {
        return repository.findGroupsByChatId(chatId, "");
    }

    /**
     * Найти все контакты, имеющееся у данного пользователя с применением сортировки
     */
    public List<Group> findGroupsByChatIdWithSorter(
            Long chatId, String sorter) {
        return repository.findGroupsByChatId(chatId, sorter);
    }

    /**
     * Попытаться обновить группу
     *
     * @param chatId  идентификатор чата
     * @param currentName имя группы до обновления
     * @param group обновляемая группа
     * @throws GroupDoesNotExistException если группа не существует
     */
    public void tryUpdateGroup(Long chatId, String currentName, Group group)
            throws GroupDoesNotExistException {
        findGroupByName(chatId, currentName).orElseThrow(() ->
                new GroupDoesNotExistException(
                        "Группа %s не существует".formatted(currentName)));
        repository.update(currentName, group);
    }

    /**
     * Удалить группу по имени
     */
    public void deleteGroupByName(Long chatId, String groupName)
            throws GroupDoesNotExistException {
        if (repository.findGroupByName(groupName, chatId).isPresent()) {
            repository.deleteByName(groupName, chatId);
        } else {
            throw new GroupDoesNotExistException(
                    "Группа %s не существует".formatted(groupName));
        }
    }
}
